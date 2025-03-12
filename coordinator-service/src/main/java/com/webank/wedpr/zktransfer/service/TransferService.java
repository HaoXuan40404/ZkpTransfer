package com.webank.wedpr.zktransfer.service;

import com.webank.wedpr.crypto.zkp.NativeInterface;
import com.webank.wedpr.crypto.zkp.WedprException;
import com.webank.wedpr.zktransfer.common.EnumResponseStatus;
import com.webank.wedpr.zktransfer.common.TransactionStatus;
import com.webank.wedpr.zktransfer.entity.TransactionHistory;
import com.webank.wedpr.zktransfer.message.ChainDepositRequest;
import com.webank.wedpr.zktransfer.message.ChainDepositResponse;
import com.webank.wedpr.zktransfer.message.ChainWithdrawRequest;
import com.webank.wedpr.zktransfer.message.ChainWithdrawResponse;
import com.webank.wedpr.zktransfer.repository.TransactionHistoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class TransferService {

    @Autowired
    private NativeInterface nativeInterface;

    @Autowired private TransactionHistoryRepository transactionHistoryRepository;

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
}
