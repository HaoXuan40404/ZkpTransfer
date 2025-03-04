package com.webank.wedpr.zktransfer.service;

import com.webank.wedpr.zktransfer.common.EnumResponseStatus;
import com.webank.wedpr.zktransfer.common.PpcException;
import com.webank.wedpr.zktransfer.message.amop.ChainDepositRequest;
import com.webank.wedpr.zktransfer.message.amop.ChainDepositResponse;
import com.webank.wedpr.zktransfer.message.amop.ChainTransferRequest;
import com.webank.wedpr.zktransfer.message.amop.ChainTransferResponse;
import com.webank.wedpr.zktransfer.message.amop.ChainWithdrawRequest;
import com.webank.wedpr.zktransfer.message.amop.ChainWithdrawResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;

@Service
@Slf4j
public class ChainService {
    @Autowired
    private FiscoBcosClient fiscoBcosClient;

    @Retryable(value = {PpcException.class}, backoff = @Backoff(delay = 2000, multiplier = 1.5))
    public ChainDepositResponse deposit(ChainDepositRequest request) throws PpcException {
        ChainDepositResponse response = new ChainDepositResponse();
        try {
            String txHash = fiscoBcosClient.mint(request.getProof(), request.getCommitment(), request.getViewKey(), request.getCipher(), request.getAmount());
            response.setTxHash(txHash);
            response.setBlockNumber(0); // You need to get the block number from the transaction receipt
            response.setStatus("success");
        } catch (ContractException e) {
            log.error("Error during deposit: ", e);
            throw new PpcException(EnumResponseStatus.FAILURE.getErrorCode(), e.getMessage());
        }
        return response;
    }

    @Retryable(value = {PpcException.class}, backoff = @Backoff(delay = 2000, multiplier = 1.5))
    public ChainWithdrawResponse withdraw(ChainWithdrawRequest request) throws PpcException {
        ChainWithdrawResponse response = new ChainWithdrawResponse();
        try {
            String txHash = fiscoBcosClient.burn(request.getProofsList(), request.getCommitment(), request.getAmount(), request.getAccount());
            response.setTxHash(txHash);
            response.setBlockNumber(0); // You need to get the block number from the transaction receipt
            response.setStatus("success");
        } catch (ContractException e) {
            log.error("Error during withdraw: ", e);
            throw new PpcException(EnumResponseStatus.FAILURE.getErrorCode(), e.getMessage());
        }
        return response;
    }

    @Retryable(value = {PpcException.class}, backoff = @Backoff(delay = 2000, multiplier = 1.5))
    public ChainTransferResponse transfer(ChainTransferRequest request) throws PpcException {
        ChainTransferResponse response = new ChainTransferResponse();
        try {
            String txHash = fiscoBcosClient.transfer(request.getInputCommitments(), request.getOutputCommitments(), request.getOutputViewKeys(), request.getOutputNoteCiphers(), request.getRelationshipProof(), request.getKnowledgeProofs(), request.getRangeProofs());
            response.setTxHash(txHash);
            response.setBlockNumber(0); // You need to get the block number from the transaction receipt
            response.setStatus("success");
        } catch (ContractException e) {
            log.error("Error during transfer: ", e);
            throw new PpcException(EnumResponseStatus.FAILURE.getErrorCode(), e.getMessage());
        }
        return response;
    }
}