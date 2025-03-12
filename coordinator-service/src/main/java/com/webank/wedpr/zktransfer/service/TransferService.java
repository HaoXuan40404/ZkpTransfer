package com.webank.wedpr.zktransfer.service;

import com.webank.wedpr.crypto.zkp.NativeInterface;
import com.webank.wedpr.crypto.zkp.WedprException;
import com.webank.wedpr.zktransfer.common.EnumResponseStatus;
import com.webank.wedpr.zktransfer.common.TransactionStatus;
import com.webank.wedpr.zktransfer.entity.Account;
import com.webank.wedpr.zktransfer.entity.TransactionHistory;
import com.webank.wedpr.zktransfer.message.*;
import com.webank.wedpr.zktransfer.message.coordinator.*;
import com.webank.wedpr.zktransfer.repository.AccountRepository;
import com.webank.wedpr.zktransfer.repository.TransactionHistoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.print.DocFlavor.STRING;

@Service
@Slf4j
public class TransferService {

    @Autowired
    private NativeInterface nativeInterface;

    @Autowired private TransactionHistoryRepository transactionHistoryRepository;

    @Autowired private AccountRepository accountRepository;

    @Autowired private CalculatorClient calculatorClient;

    @Autowired private ChainService chainService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 并发相同的rG会导致链上写入失败 但是不会导致用户资产丢失

    public ChainDepositResponse deposit(ChainDepositRequest request) throws WedprException {
        // 验证参与方的proof和value
        byte[] proof = request.getProof();
        int amount = request.getAmount();
        byte[] commitment = request.getCommitment();
        boolean verifyResult = nativeInterface.verifyValueEqualityRelationshipProof(amount, commitment, proof).expectNoError().result;
        if(!verifyResult)
        {
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
        return chainService.deposit(request);
    }

    public ChainWithdrawResponse withdraw(ChainWithdrawRequest request) throws WedprException {
        // 验证参与方的proof和value
        for(int i = 0; i < request.getAmountList().size();i++)
        {
            byte[] valueProof = request.getValueProofsList().get(i);
            byte[] knowledgeProof = request.getKnwoledProofsList().get(i);
            int amount = request.getAmountList().get(i);
            byte[] commitment = request.getCommitmentsList().get(i);
            boolean verifyResult = nativeInterface.verifyValueEqualityRelationshipProof(amount, commitment, valueProof).expectNoError().result;
            if(!verifyResult)
            {
                throw new WedprException(EnumResponseStatus.FAILURE.getMessage());
            }
            boolean verifyResultKnowledge = nativeInterface.verifyKnowledgeProof(commitment, knowledgeProof).expectNoError().result;
            if(!verifyResultKnowledge)
            {
                throw new WedprException(EnumResponseStatus.FAILURE.getMessage());
            }
        }

        // 记录历史db
        List<TransactionHistory> transactionHistories = new ArrayList<>();
        for(int i = 0; i < request.getAmountList().size();i++)
        {
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
        for(int i = 0; i < request.getAmountList().size();i++)
        {
            byte[] knowledgeProof = request.getKnwoledProofsList().get(i);
            byte[] commitment = request.getCommitmentsList().get(i);
            chainService.withdraw(knowledgeProof, commitment);
        }
        ChainWithdrawResponse response = new ChainWithdrawResponse();
        response.setErrorCode(EnumResponseStatus.SUCCESS.getErrorCode());
        response.setMessage(EnumResponseStatus.SUCCESS.getMessage());
        return response;
    }

    public void transfer(ChainTransferInitialRequest request) throws WedprException {
        // 1. 查询db t_account 拿到发送方和接收方的银行url 不存在则报错
        String fromBank = request.getAgencyName();
        String toBank = request.getReceiverBankName();
        Optional<Account> fromBankAccount = accountRepository.findByName(fromBank);
        log.info("transfer initial request, fromBank: {}, toBank: {}", fromBank, toBank);
        if(!fromBankAccount.isPresent())
        {
            log.error("fromBankAccount: {}, not exist", fromBank);
            throw new WedprException(EnumResponseStatus.FAILURE.getMessage());
        }
        Optional<Account> toBankAccount = accountRepository.findByName(toBank);
        if(!toBankAccount.isPresent())
        {
            log.error("toBankAccount: {}, not exist", toBank);
            throw new WedprException(EnumResponseStatus.FAILURE.getMessage());
        }
        String fromBankUrl = fromBankAccount.get().getUrl();
        String toBankUrl = toBankAccount.get().getUrl();

        String bizSeq = request.getBizSeq();

        // 2. 验证from的Knowledge proof、value proof，from接受的range proof，拿到commitment
        // 金额
        List<Integer> inputAmountList = request.getInputInfos().getAmountList();
        int inputAmountSum = inputAmountList.stream().mapToInt(Integer::intValue).sum();
        // 花费证明
        List<byte[]> knwoledProofsList = request.getInputInfos().getKnwoledProofsList();
        // 金额证明
        List<byte[]> valueProofsList = request.getInputInfos().getValueProofsList();
        // 承诺
        List<byte[]> commitmentsList = request.getInputInfos().getCommitmentsList();
        // 存在找零
        int fromChangeAmount = 0;
        if(request.getChangeInfos() != null)
        {
            fromChangeAmount = request.getChangeInfos().getAmount();
            // 检查接受的range proof
            if(nativeInterface.verifyRangeProof(request.getChangeInfos().getCommitment(), request.getChangeInfos().getProof()).expectNoError().result)
            {
                log.error("verifyRangeProof failed! commitment: {}, proof: {}", Hex.toHexString(request.getChangeInfos().getCommitment()), Hex.toHexString(request.getChangeInfos().getProof()));
                throw new WedprException(EnumResponseStatus.FAILURE.getMessage());
            }
        }
        // 给to的钱
        int toAmount = request.getReceiverAmount();
        // 检查明文金额
        if(inputAmountSum != (fromChangeAmount + toAmount))
        {
            log.error("balance check failed! inputAmountSum: {}, fromChangeAmount: {}, toAmount: {}", inputAmountSum, fromChangeAmount, toAmount);
            throw new WedprException(EnumResponseStatus.FAILURE.getMessage());
        }

        // 检查from的proof
        // 检查花费证明和金额证明
        for (int i = 0; i < knwoledProofsList.size(); i++) {
            if(nativeInterface.verifyKnowledgeProof(commitmentsList.get(i), knwoledProofsList.get(i)).expectNoError().result){
                log.error("verifyKnowledgeProof failed! commitment: {}, knwoledProof: {}", Hex.toHexString(commitmentsList.get(i)), Hex.toHexString(knwoledProofsList.get(i)));
                throw new WedprException(EnumResponseStatus.FAILURE.getMessage());
            }
            if(nativeInterface.verifyValueEqualityRelationshipProof(inputAmountList.get(i), commitmentsList.get(i), valueProofsList.get(i)).expectNoError().result)
            {
                log.error("verifyValueEqualityRelationshipProof failed! amount: {}, commitment: {}, valueProof: {}", inputAmountList.get(i), Hex.toHexString(commitmentsList.get(i)), Hex.toHexString(valueProofsList.get(i)));
                throw new WedprException(EnumResponseStatus.FAILURE.getMessage());
            }
        }

        // 拿到from balance proof Initial share
        byte[] inputBalanceShareInitial = request.getInputBalanceInitialShare();
        byte[] changeBalanceInitialShare = request.getChangeBalanceInitialShare();


        // 2. 调用接收方 拿到to的commitment和proof，验证接收方的range proof
        // 组装Request 通知from 收到来自 to 的 amount


        // 3. 使用所有的commitment生成check

        // 4. 调用from和to的api 拿到relationship proofshare

        // 5. 组装proof, 上链
        
        // 6. db记录历史

        // 7. 返回
        

    }
}
