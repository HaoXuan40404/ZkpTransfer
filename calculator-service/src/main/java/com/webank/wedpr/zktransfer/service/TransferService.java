package com.webank.wedpr.zktransfer.service;

import com.webank.wedpr.crypto.zkp.NativeInterface;
import com.webank.wedpr.crypto.zkp.WedprException;
import com.webank.wedpr.zktransfer.entity.CommitmentEntity;
import com.webank.wedpr.zktransfer.message.DepositRequest;
import com.webank.wedpr.zktransfer.message.WithdrawRequest;
import com.webank.wedpr.zktransfer.message.coordinator.ChainDepositRequest;
import com.webank.wedpr.zktransfer.message.coordinator.ChainWithdrawRequest;
import com.webank.wedpr.zktransfer.repository.CommitmentRepository;
import com.webank.wedpr.zktransfer.utils.CommitmentStatus;
import com.webank.wedpr.zktransfer.utils.KeyDriveFunction;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class TransferService {

    @Autowired
    CommitmentRepository commitmentRepository;

    @Autowired private NativeInterface nativeInterface;

    @Autowired private byte[] servicePrivateKey;

    public ChainDepositRequest deposit(DepositRequest request) throws NoSuchAlgorithmException, WedprException {
        // 查询db拿到用户密钥 和commitment最大的index
        Optional<CommitmentEntity> maxIndexCommitmentOpt = commitmentRepository.findMaxIndexCommitment();
        int currentIndex = 0;
        if(maxIndexCommitmentOpt.isPresent())
        {
            currentIndex = maxIndexCommitmentOpt.get().getIndex();
            currentIndex++;
        }
        // index++ 生成新的commitment和viewKey 记录到db中 status pending状态
        log.info("Current index: {}", currentIndex);
        byte[] indexBlinding = KeyDriveFunction.deriveKey(servicePrivateKey, currentIndex);
        byte[] viewKey = nativeInterface.computeViewkey(indexBlinding).expectNoError().viewkey;
        int amount = request.getAmount();
        byte[] amountBytes = AESUtils.intToBytes(amount);
        // 加密生成cipher
        byte[] cipher = AESUtils.encrypt(amountBytes, servicePrivateKey);
        // 生成commitment的proof 构造ChainDepositRequest
        byte[] commitment = nativeInterface.computeCommitment(amount, indexBlinding).expectNoError().commitment;
        byte[] proof = nativeInterface.proveValueEqualityRelationshipProof(amount, indexBlinding).expectNoError().proof;

        ChainDepositRequest chainDepositRequest = new ChainDepositRequest();
        chainDepositRequest.setCommitment(commitment);
        chainDepositRequest.setProof(proof);
        chainDepositRequest.setAmount(amount);
        chainDepositRequest.setCipher(cipher);
        chainDepositRequest.setViewKey(viewKey);

        CommitmentEntity newCommitmentEntity = new CommitmentEntity();
        String commitmentStr = Hex.toHexString(commitment);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        newCommitmentEntity.setCommitment(commitmentStr);
        newCommitmentEntity.setValue(amount);
        newCommitmentEntity.setIndex(currentIndex);
        newCommitmentEntity.setUpdateTime(timestamp);
        newCommitmentEntity.setCreateTime(timestamp);
        newCommitmentEntity.setStatus(CommitmentStatus.Pending.getValue());
        return chainDepositRequest;
    }

    public ChainWithdrawRequest withdraw(WithdrawRequest request) throws WedprException, NoSuchAlgorithmException {
        int requestedAmount = request.getAmount();
        int totalAmount = 0;

        // 查询数据库中未花费的commitment
        List<CommitmentEntity> unspentCommitments = commitmentRepository.findByStatus(CommitmentStatus.Unspent.getValue());

        // 遍历未花费的commitment，直到总金额大于等于请求的金额
        List<CommitmentEntity> selectedCommitments = new ArrayList<>();
        List<byte[]> selectedCommitmentsBytes = new ArrayList<>();
        List<Integer> selectedValues = new ArrayList<>();
        for (CommitmentEntity commitmentEntity : unspentCommitments) {
            totalAmount += commitmentEntity.getValue();
            selectedValues.add(commitmentEntity.getValue());
            selectedCommitmentsBytes.add(Hex.decode(commitmentEntity.getCommitment()));
            selectedCommitments.add(commitmentEntity);
            if (totalAmount >= requestedAmount) {
                break;
            }
        }

        if (totalAmount < requestedAmount) {
            throw new WedprException("Insufficient funds");
        }

        // 为选中的commitment生成proof
        List<byte[]> valueProofs = new ArrayList<>();
        List<byte[]> knowledgeProofs = new ArrayList<>();
        for (CommitmentEntity commitmentEntity : selectedCommitments) {
            byte[] indexBlinding = KeyDriveFunction.deriveKey(servicePrivateKey, commitmentEntity.getIndex());
            byte[] valueProof = nativeInterface.proveValueEqualityRelationshipProof(commitmentEntity.getValue(), indexBlinding).expectNoError().proof;
            byte[] knowledgeProof = nativeInterface.proveKnowledgeProof(commitmentEntity.getValue(), indexBlinding).expectNoError().proof;
            valueProofs.add(valueProof);
            knowledgeProofs.add(knowledgeProof);
        }

        // 构造ChainWithdrawRequest
        ChainWithdrawRequest chainWithdrawRequest = new ChainWithdrawRequest();
        chainWithdrawRequest.setAmountList(selectedValues);
        chainWithdrawRequest.setCommitmentsList(selectedCommitmentsBytes);
        chainWithdrawRequest.setKnwoledProofsList(knowledgeProofs);
        chainWithdrawRequest.setValueProofsList(valueProofs);

        // 调用coordinator的withdraw

        // 更新数据库中commitment的状态
        for (CommitmentEntity commitmentEntity : selectedCommitments) {
            commitmentEntity.setStatus(CommitmentStatus.Spent.getValue());
            commitmentRepository.save(commitmentEntity);
        }

        return chainWithdrawRequest;
    }

    public void updateCommitmentStatus(byte[] commitment, int status)
    {
        String commitmentStr = Hex.toHexString(commitment);
        commitmentRepository.updateStatusByCommitment(commitmentStr, status);
    }
}
