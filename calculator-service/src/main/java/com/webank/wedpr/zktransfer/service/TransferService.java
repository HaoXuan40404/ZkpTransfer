package com.webank.wedpr.zktransfer.service;

import com.webank.wedpr.crypto.zkp.NativeInterface;
import com.webank.wedpr.crypto.zkp.WedprException;
import com.webank.wedpr.crypto.zkp.ZkpResult;
import com.webank.wedpr.zktransfer.common.Utils;
import com.webank.wedpr.zktransfer.entity.CommitmentEntity;
import com.webank.wedpr.zktransfer.message.calculator.*;
import com.webank.wedpr.zktransfer.message.coordinator.MintCommitmentRequest;
import com.webank.wedpr.zktransfer.message.coordinator.BurnCommitmentRequest;
import com.webank.wedpr.zktransfer.message.coordinator.TransferCommitmentRequest;
import com.webank.wedpr.zktransfer.repository.CommitmentRepository;
import com.webank.wedpr.zktransfer.common.CommitmentStatus;
import com.webank.wedpr.zktransfer.common.KeyDriveFunction;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.*;

@Slf4j
@Service
public class TransferService {

    @Autowired
    CommitmentRepository commitmentRepository;

    @Autowired private  String agencyName;

    @Autowired private NativeInterface nativeInterface;

    @Autowired private byte[] servicePrivateKey;

    // TODO: 记录初始化的状态 记录db 多活
    private final Map<String, List<byte[]>> senderInitialPart = new HashMap<>();
    private final Map<String, byte[]> receiverInitialPart = new HashMap<>();


    public MintCommitmentRequest deposit(DepositRequest request) throws NoSuchAlgorithmException, WedprException {
        // 查询db拿到用户密钥 和commitment最大的index
        Optional<CommitmentEntity> maxIndexCommitmentOpt = commitmentRepository.findMaxIndexCommitment();
        int currentIndex = 0;
        if(maxIndexCommitmentOpt.isPresent())
        {
            currentIndex = maxIndexCommitmentOpt.get().getKdfIndex();
            currentIndex++;
        }
        // index++ 生成新的commitment和viewKey 记录到db中 status pending状态
        byte[] indexBlinding = KeyDriveFunction.deriveKey(servicePrivateKey, currentIndex);
        byte[] viewKey = nativeInterface.computeViewkey(indexBlinding).expectNoError().viewkey;
        int amount = request.getAmount();
        byte[] amountBytes = AESUtils.intToBytes(amount);
        // 加密生成cipher
        byte[] cipher = AESUtils.encrypt(amountBytes, servicePrivateKey);
        // 生成commitment的proof 构造ChainDepositRequest
        byte[] commitment = nativeInterface.computeCommitment(amount, indexBlinding).expectNoError().commitment;
        byte[] proof = nativeInterface.proveValueEqualityRelationshipProof(amount, indexBlinding).expectNoError().proof;


        MintCommitmentRequest mintCommitmentRequest = new MintCommitmentRequest();
        mintCommitmentRequest.setCommitment(commitment);
        mintCommitmentRequest.setProof(proof);
        mintCommitmentRequest.setAmount(amount);
        mintCommitmentRequest.setCipher(cipher);
        mintCommitmentRequest.setViewKey(viewKey);

        CommitmentEntity newCommitmentEntity = new CommitmentEntity();
        String commitmentStr = Hex.toHexString(commitment);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        newCommitmentEntity.setCommitment(commitmentStr);
        newCommitmentEntity.setCommitmentValue(amount);
        newCommitmentEntity.setKdfIndex(currentIndex);
        newCommitmentEntity.setUpdateTime(timestamp);
        newCommitmentEntity.setCreateTime(timestamp);
        newCommitmentEntity.setStatus(CommitmentStatus.Pending.getValue());
        commitmentRepository.save(newCommitmentEntity);
        log.info("deposit commitment {}, Current index: {} with pending", commitmentStr, currentIndex);
        return mintCommitmentRequest;
    }

    public BurnCommitmentRequest withdraw(WithdrawRequest request) throws WedprException, NoSuchAlgorithmException {
        int requestedAmount = request.getAmount();
        int totalAmount = 0;

        // 查询数据库中未花费的commitment
        List<CommitmentEntity> unspentCommitments = commitmentRepository.findByStatus(CommitmentStatus.Unspent.getValue());

        // 遍历未花费的commitment，直到总金额大于等于请求的金额
        List<CommitmentEntity> selectedCommitments = new ArrayList<>();
        List<Integer> selectedValues = new ArrayList<>();
        List<byte[]> selectedCommitmentsBytes = new ArrayList<>();
        for (CommitmentEntity commitmentEntity : unspentCommitments) {
            totalAmount += commitmentEntity.getCommitmentValue();
            selectedValues.add(commitmentEntity.getCommitmentValue());
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
            byte[] indexBlinding = KeyDriveFunction.deriveKey(servicePrivateKey, commitmentEntity.getKdfIndex());
            byte[] valueProof = nativeInterface.proveValueEqualityRelationshipProof(commitmentEntity.getCommitmentValue(), indexBlinding).expectNoError().proof;
            byte[] knowledgeProof = nativeInterface.proveKnowledgeProof(commitmentEntity.getCommitmentValue(), indexBlinding).expectNoError().proof;
            valueProofs.add(valueProof);
            knowledgeProofs.add(knowledgeProof);
//            selectedCommitmentsBytes.add(Hex.decode(commitmentEntity.getCommitment()));
        }

        // 构造ChainWithdrawRequest
        BurnCommitmentRequest burnCommitmentRequest = new BurnCommitmentRequest();
        burnCommitmentRequest.setAmountList(selectedValues);
        burnCommitmentRequest.setCommitmentsList(selectedCommitmentsBytes);
        burnCommitmentRequest.setKnowledgeProofsList(knowledgeProofs);
        burnCommitmentRequest.setValueProofsList(valueProofs);

        // 调用coordinator的withdraw

        // 更新数据库中commitment的状态
        for (CommitmentEntity commitmentEntity : selectedCommitments) {
            commitmentEntity.setStatus(CommitmentStatus.Spent.getValue());
            commitmentRepository.save(commitmentEntity);
        }

        return burnCommitmentRequest;
    }

    public void updateCommitmentStatus(byte[] commitment, int status)
    {
        String commitmentStr = Hex.toHexString(commitment);
        commitmentRepository.updateStatusByCommitment(commitmentStr, status);
    }

    // 转账
    public TransferRecord transferInitiate(TransferRequest request)
            throws WedprException, NoSuchAlgorithmException {
        // from一定是自己
        String fromBank = agencyName;
        String bizSeq = Utils.getUuid();
        String toBank = request.getReceiverBankInfo();
        int transferAmount = request.getSpendAmountList();
        log.info("Initiating transfer from {} to {} with amount {}", fromBank, toBank, transferAmount);

        int totalAmount = 0;
        // 查询数据库中未花费的commitment
        List<CommitmentEntity> unspentCommitments = commitmentRepository.findByStatus(CommitmentStatus.Unspent.getValue());

        // 遍历未花费的commitment,直到总金额大于等于请求的金额
        List<CommitmentEntity> selectedCommitments = new ArrayList<>();
        List<byte[]> selectedCommitmentsBytes = new ArrayList<>();
        List<Integer> selectedValues = new ArrayList<>();
        List<byte[]> selectedindexBlinding = new ArrayList<>();
        for (CommitmentEntity commitmentEntity : unspentCommitments) {
            totalAmount += commitmentEntity.getCommitmentValue();
            selectedValues.add(commitmentEntity.getCommitmentValue());
            selectedCommitmentsBytes.add(Hex.decode(commitmentEntity.getCommitment()));
            selectedCommitments.add(commitmentEntity);
            if (totalAmount >= transferAmount) {
                break;
            }
        }

        //检查余额是否足够；
        if (totalAmount < transferAmount) {
            throw new WedprException("Insufficient funds");
        }

        // 为选中的commitment生成proof
        List<byte[]> valueProofs = new ArrayList<>();
        List<byte[]> knowledgeProofs = new ArrayList<>();
        List<byte[]> selectedSetupPublicPartList = new ArrayList<>();
        // TODO:
        List<byte[]> selectedSetupPrivatePartList = new ArrayList<>();
        for (CommitmentEntity commitmentEntity : selectedCommitments) {
            byte[] indexBlinding = KeyDriveFunction.deriveKey(servicePrivateKey, commitmentEntity.getKdfIndex());
            selectedindexBlinding.add(indexBlinding);
            byte[] valueProof = nativeInterface.proveValueEqualityRelationshipProof(commitmentEntity.getCommitmentValue(), indexBlinding).expectNoError().proof;
            byte[] knowledgeProof = nativeInterface.proveKnowledgeProof(commitmentEntity.getCommitmentValue(), indexBlinding).expectNoError().proof;
//            log.info("knowledgeProof :{}, commitment: {}", Hex.toHexString(knowledgeProof), commitmentEntity.getCommitment());
//            boolean knowledgeVerifyResult = nativeInterface.verifyKnowledgeProof(Hex.decode(commitmentEntity.getCommitment()), knowledgeProof).expectNoError().result;
//            log.info("knowledgeVerifyResult :{}", knowledgeVerifyResult);

            valueProofs.add(valueProof);
            knowledgeProofs.add(knowledgeProof);
            //标记为pending状态
            commitmentEntity.setStatus(CommitmentStatus.Pending.getValue());
            commitmentRepository.save(commitmentEntity);
            //生成选中commitment的setup Proof
            ZkpResult selectedSetupResult = nativeInterface.senderProveMultiSumRelationshipSetup(
                    commitmentEntity.getCommitmentValue(), indexBlinding).expectNoError();
            selectedSetupPublicPartList.add(selectedSetupResult.publicPart);
            selectedSetupPrivatePartList.add(selectedSetupResult.privatePart);
        }
        if(senderInitialPart.containsKey(bizSeq))
        {
            log.error("Sender initial part already exists, key {}", bizSeq);
            throw new WedprException("Sender initial part already exists");
        }
        senderInitialPart.put(bizSeq, selectedSetupPrivatePartList);

        // 计算找零
        int changeAmount = totalAmount - transferAmount;

        // 查询db拿到用户密钥 和commitment最大的index
        Optional<CommitmentEntity> maxIndexCommitmentOpt = commitmentRepository.findMaxIndexCommitment();
        int currentIndex = 0;
        if (maxIndexCommitmentOpt.isPresent()) {
            currentIndex = maxIndexCommitmentOpt.get().getKdfIndex();
            currentIndex++;
        }

        // index++ 生成新的commitment和viewKey 记录到db中 status pending状态
        byte[] changeIndexBlinding = KeyDriveFunction.deriveKey(servicePrivateKey, currentIndex);
        byte[] changeViewKey = nativeInterface.computeViewkey(changeIndexBlinding).expectNoError().viewkey;
        byte[] changeAmountBytes = AESUtils.intToBytes(changeAmount);
        byte[] changeCipher = AESUtils.encrypt(changeAmountBytes, servicePrivateKey);
        byte[] changeCommitment = nativeInterface.computeCommitment(changeAmount, changeIndexBlinding).expectNoError().commitment;
        byte[] changeProof = nativeInterface.proveValueEqualityRelationshipProof(changeAmount, changeIndexBlinding).expectNoError().proof;
        byte[] rangeProof = nativeInterface.proveRangeProof(changeAmount, changeIndexBlinding).expectNoError().proof;
        //生成找零commitment的setupProof
        ZkpResult changeSetup = nativeInterface.receiverProveMultiSumRelationshipSetup(
                changeAmount, changeIndexBlinding).expectNoError();
        if(receiverInitialPart.containsKey(bizSeq))
        {
            log.error("Change initial part already exists, key {}", bizSeq);
            throw new WedprException("Change initial part already exists");
                }
        receiverInitialPart.put(bizSeq, changeSetup.privatePart);


        // 保存找零commitment到数据库
        CommitmentEntity changeCommitmentEntity = new CommitmentEntity();
        String changeCommitmentStr = Hex.toHexString(changeCommitment);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        changeCommitmentEntity.setCommitment(changeCommitmentStr);
        changeCommitmentEntity.setCommitmentValue(changeAmount);
        changeCommitmentEntity.setKdfIndex(currentIndex);
        changeCommitmentEntity.setUpdateTime(timestamp);
        changeCommitmentEntity.setCreateTime(timestamp);
        changeCommitmentEntity.setStatus(CommitmentStatus.Pending.getValue());
        commitmentRepository.save(changeCommitmentEntity);

        // 构建转账请求
        TransferCommitmentRequest transferRequest = new TransferCommitmentRequest();
        transferRequest.setAgencyName(fromBank);
        transferRequest.setReceiverBankName(toBank);
        transferRequest.setReceiverAmount(transferAmount);
        transferRequest.setInputBalanceInitialShares(selectedSetupPublicPartList);
        transferRequest.setChangeBalanceInitialShare(changeSetup.publicPart);
        transferRequest.setAgencyName(agencyName);
        transferRequest.setBizSeq(bizSeq);

        // 设置 inputInfos (ChainWithdrawRequest)
        BurnCommitmentRequest inputInfos = new BurnCommitmentRequest();
        inputInfos.setValueProofsList(valueProofs);
        inputInfos.setKnowledgeProofsList(knowledgeProofs);
        inputInfos.setCommitmentsList(selectedCommitmentsBytes);
        inputInfos.setAmountList(selectedValues);
        transferRequest.setInputInfos(inputInfos);

        // 设置 changeInfos (ChainDepositRequest)
        MintCommitmentRequest changeInfos = new MintCommitmentRequest();
        changeInfos.setProof(changeProof);
        changeInfos.setCommitment(changeCommitment);
        changeInfos.setViewKey(changeViewKey);
        changeInfos.setCipher(changeCipher);
        changeInfos.setAmount(changeAmount);
        changeInfos.setRangeProof(rangeProof);

        transferRequest.setChangeInfos(changeInfos);

        TransferRecord transferRecord = new TransferRecord();
        transferRecord.setRole("from");
        transferRecord.setSenderRequest(transferRequest);
        transferRecord.setSenderBlinding(selectedindexBlinding);
        transferRecord.setReceiverBlinding(changeIndexBlinding);

        log.info("Transfer initiated successfully");
        return transferRecord;
    }


    public TransferRecord transferNotify(TransferReceiveRequest request) throws WedprException, NoSuchAlgorithmException {
        // 查询db拿到用户密钥 和commitment最大的index
        Optional<CommitmentEntity> maxIndexCommitmentOpt = commitmentRepository.findMaxIndexCommitment();
        int currentIndex = 0;
        if(maxIndexCommitmentOpt.isPresent())
        {
            currentIndex = maxIndexCommitmentOpt.get().getKdfIndex();
            currentIndex++;
        }
        // index++ 生成新的commitment和viewKey 记录到db中 status pending状态
        byte[] indexBlinding = KeyDriveFunction.deriveKey(servicePrivateKey, currentIndex);
        byte[] viewKey = nativeInterface.computeViewkey(indexBlinding).expectNoError().viewkey;
        int amount = request.getReceiveAmount();
        byte[] amountBytes = AESUtils.intToBytes(amount);
        // 加密生成cipher
        byte[] cipher = AESUtils.encrypt(amountBytes, servicePrivateKey);
        // 生成commitment的proof 构造TransferReceiveResponse
        byte[] commitment = nativeInterface.computeCommitment(amount, indexBlinding).expectNoError().commitment;
        byte[] proof = nativeInterface.proveValueEqualityRelationshipProof(amount, indexBlinding).expectNoError().proof;
        byte[] rangeProof = nativeInterface.proveRangeProof(amount, indexBlinding).expectNoError().proof;

        ZkpResult receiverProofShare = nativeInterface.receiverProveMultiSumRelationshipSetup(amount, indexBlinding);
        if(receiverInitialPart.containsKey(request.getBizSeq()))
        {
            log.error("Receiver initial part already exists, key {}", request.getBizSeq());
            throw new WedprException("Receiver initial part already exists");
        }
        receiverInitialPart.put(request.getBizSeq(), receiverProofShare.privatePart);


        MintCommitmentRequest chainDepositRequest = new MintCommitmentRequest();
        chainDepositRequest.setCommitment(commitment);
        chainDepositRequest.setProof(proof);
        chainDepositRequest.setAmount(amount);
        chainDepositRequest.setCipher(cipher);
        chainDepositRequest.setViewKey(viewKey);
        chainDepositRequest.setRangeProof(rangeProof);
        TransferReceiveResponse transferReceiveResponse = new TransferReceiveResponse();
        transferReceiveResponse.setReceiveProof(chainDepositRequest);

        CommitmentEntity newCommitmentEntity = new CommitmentEntity();
        String commitmentStr = Hex.toHexString(commitment);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        newCommitmentEntity.setCommitment(commitmentStr);
        newCommitmentEntity.setCommitmentValue(amount);
        newCommitmentEntity.setKdfIndex(currentIndex);
        newCommitmentEntity.setUpdateTime(timestamp);
        newCommitmentEntity.setCreateTime(timestamp);
        newCommitmentEntity.setStatus(CommitmentStatus.Pending.getValue());
        commitmentRepository.save(newCommitmentEntity);
        log.info("receive commitment {}, Current index: {} with pending", commitmentStr, currentIndex);

        TransferRecord transferRecord = new TransferRecord();
        transferRecord.setRole("to");
        transferRecord.setReceiverBlinding(indexBlinding);
        transferRecord.setReceiverResponse(transferReceiveResponse);
        transferRecord.getReceiverResponse().setBalanceInitialShare(receiverProofShare.publicPart);
        return transferRecord;
    }


    public List<byte[]> transferSenderComplete(TransferCompleteRequest request, List<byte[]> blindingList, List<Integer> valueList)
            throws WedprException, NoSuchAlgorithmException {

        List<byte[]> senderSetupFinalPartList = new ArrayList<>();
        List<byte[]> senderSetupSetupPartList = senderInitialPart.get(request.getBizSeq());
        senderInitialPart.remove(request.getBizSeq());

        for (int i = 0; i < blindingList.size(); i++) {
            ZkpResult senderFinalResult = nativeInterface.senderProveMultiSumRelationshipFinal(
                    valueList.get(i), blindingList.get(i), senderSetupSetupPartList.get(i), request.getCheck()).expectNoError();
            senderSetupFinalPartList.add(senderFinalResult.publicPart);
        }

        return senderSetupFinalPartList;
    }

    public byte[] transferReceiverComplete(TransferCompleteRequest request, byte[] blinding)
            throws WedprException {

        byte[] receiverSetup = receiverInitialPart.get(request.getBizSeq());
        receiverInitialPart.remove(request.getBizSeq());
        byte[] receiverFinal = nativeInterface.receiverProveMultiSumRelationshipFinal(
                blinding, receiverSetup, request.getCheck()).expectNoError().publicPart;

        return receiverFinal;
    }
}
