package com.webank.wedpr.zktransfer.service;

import com.webank.wedpr.crypto.zkp.WedprException;
import com.webank.wedpr.zktransfer.common.EnumResponseStatus;
import com.webank.wedpr.zktransfer.message.*;

import com.webank.wedpr.zktransfer.message.coordinator.MintCommitmentRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.List;

import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;

@Service
@Slf4j
public class ChainService {
    @Autowired
    private FiscoBcosClient fiscoBcosClient;

    private void setSuccessMsg(BaseResponse response) {
        response.setErrorCode(EnumResponseStatus.SUCCESS.getErrorCode());
        response.setMessage(EnumResponseStatus.SUCCESS.getMessage());
    }

    private void setErrorMsg(BaseResponse response) {
        response.setErrorCode(EnumResponseStatus.FAILURE.getErrorCode());
        response.setMessage(EnumResponseStatus.FAILURE.getMessage());
    }

    @Retryable(value = {WedprException.class}, backoff = @Backoff(delay = 2000, multiplier = 1.5))
    public void deposit(byte[] commitment, byte[] viewKey, byte[] cipher) throws WedprException {
        BaseResponse response = new BaseResponse();
        try {
            fiscoBcosClient.mint(commitment, viewKey, cipher);
            setSuccessMsg(response);
        } catch (ContractException e) {
            log.error("Error during deposit: ", e);
            throw new WedprException(e.getMessage());
        }
    }

    @Retryable(value = {WedprException.class}, backoff = @Backoff(delay = 2000, multiplier = 1.5))
    public void withdraw(byte[] proof, byte[] commitment) throws WedprException {
        BaseResponse response = new BaseResponse();
        try {
            fiscoBcosClient.burn(proof, commitment);
        } catch (ContractException e) {
            log.error("Error during withdraw: ", e);
            throw new WedprException(e.getMessage());
        }
    }

   @Retryable(value = {WedprException.class}, backoff = @Backoff(delay = 2000, multiplier = 1.5))
   public void transfer(List<byte[]> inputCommitments, List<byte[]> outputCommitments, List<byte[]> outputViewKeys, List<byte[]> outputNoteCiphers, byte[] relationshipProof, List<byte[]> knowledgeProofs, List<byte[]> rangeProofs) throws WedprException {
       BaseResponse response = new BaseResponse();
       try {
           fiscoBcosClient.transfer(inputCommitments, outputCommitments, outputViewKeys, outputNoteCiphers, relationshipProof, knowledgeProofs, rangeProofs);
       } catch (ContractException e) {
           log.error("Error during transfer: ", e);
           throw new WedprException(e.getMessage());
       }
   }

}