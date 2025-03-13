package com.webank.wedpr.zktransfer.service;

import com.webank.wedpr.crypto.zkp.WedprException;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ChainService {
    @Autowired
    private FiscoBcosClient fiscoBcosClient;

    @Retryable(value = {WedprException.class}, backoff = @Backoff(delay = 2000, multiplier = 1.5))
    public byte[] getCipherByViewKey(byte[] viewKey) throws WedprException {
        try {
            return fiscoBcosClient.getCipherByViewKey(viewKey);
        } catch (ContractException e) {
            log.error("Error during getCipherByViewKey: ", e);
            throw new WedprException(e.getMessage());
        }
    }

    @Retryable(value = {WedprException.class}, backoff = @Backoff(delay = 2000, multiplier = 1.5))
    public int getCommitmentStatus(byte[] commitment) throws WedprException {
        try {
            return fiscoBcosClient.getCommitmentStatus(commitment);
        } catch (ContractException e) {
            log.error("Error during getCommitmentStatus: ", e);
            throw new WedprException(e.getMessage());
        }
    }

}