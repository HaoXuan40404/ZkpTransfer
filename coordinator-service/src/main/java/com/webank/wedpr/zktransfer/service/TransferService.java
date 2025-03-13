package com.webank.wedpr.zktransfer.service;

import com.webank.wedpr.crypto.zkp.NativeInterface;
import com.webank.wedpr.crypto.zkp.WedprException;
import com.webank.wedpr.zktransfer.common.EnumResponseStatus;
import com.webank.wedpr.zktransfer.common.TransactionStatus;
import com.webank.wedpr.zktransfer.entity.Account;
import com.webank.wedpr.zktransfer.entity.TransactionHistory;
import com.webank.wedpr.zktransfer.message.BaseResponse;
import com.webank.wedpr.zktransfer.message.calculator.TransferCompleteRequest;
import com.webank.wedpr.zktransfer.message.calculator.TransferCompleteResponse;
import com.webank.wedpr.zktransfer.message.calculator.TransferReceiveRequest;
import com.webank.wedpr.zktransfer.message.calculator.TransferReceiveResponse;
import com.webank.wedpr.zktransfer.message.coordinator.*;
import com.webank.wedpr.zktransfer.repository.AccountRepository;
import com.webank.wedpr.zktransfer.repository.TransactionHistoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.webank.wedpr.crypto.zkp.ZkpDemo.concatBytesArray;

@Service
@Slf4j
public class TransferService {

    @Autowired
    private NativeInterface nativeInterface;

    @Autowired
    private TransactionHistoryRepository transactionHistoryRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CalculatorClient calculatorClient;

    @Autowired
    private ChainService chainService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 并发相同的rG会导致链上写入失败 但是不会导致用户资产丢失

    public BaseResponse deposit(MintCommitmentRequest request) throws WedprException {
        // 验证参与方的proof和value
        byte[] proof = request.getProof();
        int amount = request.getAmount();
        byte[] commitment = request.getCommitment();
        boolean verifyResult = nativeInterface.verifyValueEqualityRelationshipProof(amount, commitment, proof)
                .expectNoError().result;
        if (!verifyResult) {
            throw new WedprException(EnumResponseStatus.FAILURE.getMessage());
        }
        // 记录历史db
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        TransactionHistory transactionHistory = new TransactionHistory();
        transactionHistory.setBizSeq(request.getBizSeq());
        transactionHistory.setOwner(request.getAgencyName());
        transactionHistory.setValue(amount);
        transactionHistory.setCommitment(Hex.toHexString(commitment));
        transactionHistory.setCreateTime(timestamp);
        transactionHistory.setTransType(TransactionStatus.Deposit.getValue());
        transactionHistoryRepository.save(transactionHistory);
        // 上链
        chainService.deposit(request.getCommitment(), request.getViewKey(), request.getCipher());
        BaseResponse response = new BaseResponse();
        response.setErrorCode(EnumResponseStatus.SUCCESS.getErrorCode());
        response.setMessage(EnumResponseStatus.SUCCESS.getMessage());
        return response;
    }

    public BaseResponse withdraw(BurnCommitmentRequest request) throws WedprException {
        // 验证参与方的proof和value
        for (int i = 0; i < request.getAmountList().size(); i++) {
            byte[] valueProof = request.getValueProofsList().get(i);
            byte[] knowledgeProof = request.getKnwoledProofsList().get(i);
            int amount = request.getAmountList().get(i);
            byte[] commitment = request.getCommitmentsList().get(i);
            boolean verifyResult = nativeInterface.verifyValueEqualityRelationshipProof(amount, commitment, valueProof)
                    .expectNoError().result;
            if (!verifyResult) {
                throw new WedprException(EnumResponseStatus.FAILURE.getMessage());
            }
            boolean verifyResultKnowledge = nativeInterface.verifyKnowledgeProof(commitment, knowledgeProof)
                    .expectNoError().result;
            if (!verifyResultKnowledge) {
                throw new WedprException(EnumResponseStatus.FAILURE.getMessage());
            }
        }

        // 记录历史db
        List<TransactionHistory> transactionHistories = new ArrayList<>();
        for (int i = 0; i < request.getAmountList().size(); i++) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            TransactionHistory transactionHistory = new TransactionHistory();
            transactionHistory.setBizSeq(request.getBizSeq());
            transactionHistory.setOwner(request.getAgencyName());
            transactionHistory.setValue(request.getAmountList().get(i));
            transactionHistory.setCommitment(Hex.toHexString(request.getCommitmentsList().get(i)));
            transactionHistory.setCreateTime(timestamp);
            transactionHistory.setTransType(TransactionStatus.Withdraw.getValue());
            transactionHistories.add(transactionHistory);
        }
        transactionHistoryRepository.saveAll(transactionHistories);

        // 上链
        for (int i = 0; i < request.getAmountList().size(); i++) {
            byte[] knowledgeProof = request.getKnwoledProofsList().get(i);
            byte[] commitment = request.getCommitmentsList().get(i);
            chainService.withdraw(knowledgeProof, commitment);
        }
        BaseResponse response = new BaseResponse();
        response.setErrorCode(EnumResponseStatus.SUCCESS.getErrorCode());
        response.setMessage(EnumResponseStatus.SUCCESS.getMessage());
        return response;
    }

    // TODO: 可减少range proof 增加效率 hkma来验证value
    public BaseResponse transfer(TransferCommitmentRequest request) throws WedprException {
        // 1. 查询db t_account 拿到发送方和接收方的银行url 不存在则报错
        String fromBank = request.getAgencyName();
        String toBank = request.getReceiverBankName();
        Optional<Account> fromBankAccount = accountRepository.findByName(fromBank);
        log.info("transfer initial request, fromBank: {}, toBank: {}", fromBank, toBank);
        if (fromBankAccount.isEmpty()) {
            log.error("fromBankAccount: {}, not exist", fromBank);
            throw new WedprException(EnumResponseStatus.FAILURE.getMessage());
        }
        Optional<Account> toBankAccount = accountRepository.findByName(toBank);
        if (toBankAccount.isEmpty()) {
            log.error("toBankAccount: {}, not exist", toBank);
            throw new WedprException(EnumResponseStatus.FAILURE.getMessage());
        }
        String fromBankUrl = fromBankAccount.get().getUrl();
        String toBankUrl = toBankAccount.get().getUrl();

        // 2. 验证from的Knowledge proof、value proof，from接受的range proof，拿到commitment
        // 金额
        List<Integer> inputAmountList = request.getInputInfos().getAmountList();
        int inputAmountSum = inputAmountList.stream().mapToInt(Integer::intValue).sum();
        // 花费证明
        List<byte[]> knwoledProofsList = request.getInputInfos().getKnwoledProofsList();
        // 金额证明
        List<byte[]> valueProofsList = request.getInputInfos().getValueProofsList();
        // 消费掉的 commitment
        List<byte[]> inputCommitmentsList = request.getInputInfos().getCommitmentsList();
        // 新产生的commitment
        List<byte[]> outputCommitmentsList = new ArrayList<>();
        // 新产生的range proof
        List<byte[]> rangeProofList = new ArrayList<>();
        // viewkey 和 cipher
        List<byte[]> viewKeyList = new ArrayList<>();
        List<byte[]> cipherList = new ArrayList<>();
        // 存在找零
        int fromChangeAmount = 0;
        if (request.getChangeInfos() != null) {
            fromChangeAmount = request.getChangeInfos().getAmount();
            // 检查接受的range proof
            if (nativeInterface
                    .verifyRangeProof(request.getChangeInfos().getCommitment(), request.getChangeInfos().getProof())
                    .expectNoError().result) {
                log.error("verifyRangeProof failed! commitment: {}, proof: {}",
                        Hex.toHexString(request.getChangeInfos().getCommitment()),
                        Hex.toHexString(request.getChangeInfos().getProof()));
                throw new WedprException(EnumResponseStatus.FAILURE.getMessage());
            }
            outputCommitmentsList.add(request.getChangeInfos().getCommitment());
            rangeProofList.add(request.getChangeInfos().getRangeProof());
            viewKeyList.add(request.getChangeInfos().getViewKey());
            cipherList.add((request.getChangeInfos().getCipher()));
        }
        // 给to的钱
        int toAmount = request.getReceiverAmount();
        // 检查明文金额
        if (inputAmountSum != (fromChangeAmount + toAmount)) {
            log.error("balance check failed! inputAmountSum: {}, fromChangeAmount: {}, toAmount: {}", inputAmountSum,
                    fromChangeAmount, toAmount);
            throw new WedprException(EnumResponseStatus.FAILURE.getMessage());
        }

        // 检查from的proof
        // 检查花费证明和金额证明
        for (int i = 0; i < knwoledProofsList.size(); i++) {
            if (nativeInterface.verifyKnowledgeProof(inputCommitmentsList.get(i), knwoledProofsList.get(i))
                    .expectNoError().result) {
                log.error("verifyKnowledgeProof failed! commitment: {}, knwoledProof: {}",
                        Hex.toHexString(inputCommitmentsList.get(i)), Hex.toHexString(knwoledProofsList.get(i)));
                throw new WedprException(EnumResponseStatus.FAILURE.getMessage());
            }
            if (nativeInterface.verifyValueEqualityRelationshipProof(inputAmountList.get(i),
                    inputCommitmentsList.get(i), valueProofsList.get(i)).expectNoError().result) {
                log.error("verifyValueEqualityRelationshipProof failed! amount: {}, commitment: {}, valueProof: {}",
                        inputAmountList.get(i), Hex.toHexString(inputCommitmentsList.get(i)),
                        Hex.toHexString(valueProofsList.get(i)));
                throw new WedprException(EnumResponseStatus.FAILURE.getMessage());
            }
        }

        // 拿到from balance proof Initial share
        List<byte[]> inputBalanceInitialShares = request.getInputBalanceInitialShares();
        byte[] changeBalanceInitialShare = request.getChangeBalanceInitialShare();

        // 2. 调用接收方 拿到to的commitment和proof，验证接收方的range proof
        // 组装Request 通知from 收到来自 to 的 amount
        TransferReceiveRequest transferReceiveRequest = new TransferReceiveRequest();
        transferReceiveRequest.setReceiveAmount(toAmount);
        transferReceiveRequest.setFromBankInfo(fromBank);
        transferReceiveRequest.setBizSeq(request.getBizSeq());
        TransferReceiveResponse transferReceiveResponse = calculatorClient.transferReceive(toBankUrl,
                transferReceiveRequest);
        byte[] receiveResponseBalanceInitialShare = transferReceiveResponse.getBalanceInitialShare();
        MintCommitmentRequest receiveProof = transferReceiveResponse.getReceiveProof();
        // 验证接收方的proof
        if (nativeInterface.verifyValueEqualityRelationshipProof(receiveProof.getAmount(), receiveProof.getCommitment(),
                receiveProof.getProof()).expectNoError().result) {
            log.error("verifyValueEqualityRelationshipProof failed! amout: {}, commitment: {}, proof: {}",
                    receiveProof.getAmount(), Hex.toHexString(receiveProof.getCommitment()),
                    Hex.toHexString(receiveProof.getProof()));
            throw new WedprException(EnumResponseStatus.FAILURE.getMessage());
        }
        outputCommitmentsList.add(transferReceiveResponse.getReceiveProof().getCommitment());
        rangeProofList.add(transferReceiveResponse.getReceiveProof().getRangeProof());
        viewKeyList.add(transferReceiveResponse.getReceiveProof().getViewKey());
        cipherList.add(transferReceiveResponse.getReceiveProof().getCipher());

        // 3. 使用所有的commitment生成check
        // TODO: proof里记录了commitment，可以减少一次commitment的传输开销
        List<byte[]> outputBalanceInitialShares = new ArrayList<>();
        if (changeBalanceInitialShare.length != 0) {
            outputBalanceInitialShares.add(changeBalanceInitialShare);
        }
        outputBalanceInitialShares.add(receiveResponseBalanceInitialShare);
        byte[] check = nativeInterface
                .coordinatorProveMultiSumRelationshipSetup(concatBytesArray(inputBalanceInitialShares),
                        concatBytesArray(outputBalanceInitialShares))
                .expectNoError().check;
        // 4. 调用from和to的api 拿到relationship proof share
        TransferCompleteRequest transferCompleteRequest = new TransferCompleteRequest();
        transferCompleteRequest.setCheck(check);
        transferCompleteRequest.setBizSeq(request.getBizSeq());
        TransferCompleteResponse fromTransferCompleteResponse = calculatorClient.transferComplete(fromBankUrl,
                transferCompleteRequest);
        TransferCompleteResponse toTransferCompleteResponse = calculatorClient.transferComplete(toBankUrl,
                transferCompleteRequest);
        List<byte[]> inputRelationShipProofShares = fromTransferCompleteResponse.getInputRelationShipProofShare();
        List<byte[]> outputRelationShipProofShares = new ArrayList<>();
        if (fromTransferCompleteResponse.getOutputRelationShipProofShare().length != 0) {
            outputRelationShipProofShares.add((fromTransferCompleteResponse.getOutputRelationShipProofShare()));
        }
        outputRelationShipProofShares.add(toTransferCompleteResponse.getOutputRelationShipProofShare());

        // 5. 组装proof, 上链
        byte[] relationShipProof = nativeInterface.coordinatorProveMultiSumRelationshipFinal(
                check, concatBytesArray(inputRelationShipProofShares), concatBytesArray(outputRelationShipProofShares))
                .expectNoError().proof;

        chainService.transfer(inputCommitmentsList, outputCommitmentsList, viewKeyList, cipherList, relationShipProof,
                knwoledProofsList, rangeProofList);
        // 6. db记录历史
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        List<TransactionHistory> transactionHistories = new ArrayList<>();
        for (int i = 0; i < inputAmountList.size(); i++) {
            TransactionHistory transactionHistory = new TransactionHistory();
            transactionHistory.setBizSeq(request.getBizSeq());
            transactionHistory.setOwner(fromBank);
            transactionHistory.setValue(inputAmountList.get(i));
            transactionHistory.setCommitment(Hex.toHexString(inputCommitmentsList.get(i)));
            transactionHistory.setCreateTime(timestamp);
            transactionHistory.setTransType(TransactionStatus.TransferIn.getValue());
            transactionHistories.add(transactionHistory);
        }
        if (fromChangeAmount != 0) {
            TransactionHistory transactionHistory = new TransactionHistory();
            transactionHistory.setBizSeq(request.getBizSeq());
            transactionHistory.setOwner(fromBank);
            transactionHistory.setValue(fromChangeAmount);
            transactionHistory.setCommitment(Hex.toHexString(request.getChangeInfos().getCommitment()));
            transactionHistory.setCreateTime(timestamp);
            transactionHistory.setTransType(TransactionStatus.TransferOut.getValue());
            transactionHistories.add(transactionHistory);
        }
        TransactionHistory transactionHistory = new TransactionHistory();
        transactionHistory.setBizSeq(request.getBizSeq());
        transactionHistory.setOwner(toBank);
        transactionHistory.setValue(toAmount);
        transactionHistory.setCommitment(Hex.toHexString(outputCommitmentsList.get(outputCommitmentsList.size() - 1)));
        transactionHistory.setCreateTime(timestamp);
        transactionHistory.setTransType(TransactionStatus.TransferOut.getValue());
        transactionHistories.add(transactionHistory);
        transactionHistoryRepository.saveAll(transactionHistories);
        // 7. 返回
        BaseResponse response = new BaseResponse();
        response.setErrorCode(EnumResponseStatus.SUCCESS.getErrorCode());
        response.setMessage(EnumResponseStatus.SUCCESS.getMessage());
        return response;
    }

    public List<TransactionHistoryData> queryHistory(int page, int pageSize) {
        // 分页查询history
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<TransactionHistory> transactionHistories = transactionHistoryRepository.findAll(pageable);
        return transactionHistories.getContent().stream()
                .map(history -> new TransactionHistoryData(
                        history.getBizSeq(),
                        history.getOwner(),
                        history.getValue(),
                        history.getCommitment(),
                        history.getCreateTime(),
                        history.getTransType()))
                .collect(Collectors.toList());
    }
}
