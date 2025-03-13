package com.webank.wedpr.zktransfer.service;

import com.webank.wedpr.crypto.zkp.WedprException;
import com.webank.wedpr.zktransfer.common.EnumResponseStatus;
import com.webank.wedpr.zktransfer.common.PpcException;
import com.webank.wedpr.zktransfer.message.*;

import com.webank.wedpr.zktransfer.message.coordinator.ChainDepositRequest;
import com.webank.wedpr.zktransfer.message.coordinator.ChainDepositResponse;
import com.webank.wedpr.zktransfer.message.coordinator.ChainTransferResponse;
import com.webank.wedpr.zktransfer.message.coordinator.ChainWithdrawResponse;
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

    @Retryable(value = {PpcException.class}, backoff = @Backoff(delay = 2000, multiplier = 1.5))
    public ChainDepositResponse deposit(ChainDepositRequest request) throws WedprException {
        ChainDepositResponse response = new ChainDepositResponse();
        try {
            fiscoBcosClient.mint(request.getCommitment(), request.getViewKey(), request.getCipher());
            setSuccessMsg(response);
        } catch (ContractException e) {
            log.error("Error during deposit: ", e);
            throw new WedprException(e.getMessage());
        }
        return response;
    }

    @Retryable(value = {PpcException.class}, backoff = @Backoff(delay = 2000, multiplier = 1.5))
    public ChainWithdrawResponse withdraw(byte[] proof, byte[] commitment) throws WedprException {
        ChainWithdrawResponse response = new ChainWithdrawResponse();
        try {
            fiscoBcosClient.burn(proof, commitment);
        } catch (ContractException e) {
            log.error("Error during withdraw: ", e);
            throw new WedprException(e.getMessage());
        }
        return response;
    }

   @Retryable(value = {PpcException.class}, backoff = @Backoff(delay = 2000, multiplier = 1.5))
   public BaseResponse transfer(List<byte[]> inputCommitments, List<byte[]> outputCommitments, List<byte[]> outputViewKeys, List<byte[]> outputNoteCiphers, byte[] relationshipProof, List<byte[]> knowledgeProofs, List<byte[]> rangeProofs) throws WedprException {
       BaseResponse response = new ChainTransferResponse();
       try {
           fiscoBcosClient.transfer(inputCommitments, outputCommitments, outputViewKeys, outputNoteCiphers, relationshipProof, knowledgeProofs, rangeProofs);
       } catch (ContractException e) {
           log.error("Error during transfer: ", e);
           throw new WedprException(e.getMessage());
       }
       return response;
   }

}