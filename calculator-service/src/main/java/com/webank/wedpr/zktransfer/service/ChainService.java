package com.webank.wedpr.zktransfer.service;

import com.webank.wedpr.zktransfer.common.EnumResponseStatus;
import com.webank.wedpr.zktransfer.common.PpcException;
import com.webank.wedpr.zktransfer.message.amop.*;
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

    @Retryable(value = {PpcException.class}, backoff = @Backoff(delay = 2000, multiplier = 1.5))
    public byte[] getCipherByViewKey(byte[] viewKey) throws PpcException {
        try {
            return fiscoBcosClient.getCipherByViewKey(viewKey);
        } catch (ContractException e) {
            log.error("Error during getCipherByViewKey: ", e);
            throw new PpcException(EnumResponseStatus.FAILURE.getErrorCode(), e.getMessage());
        }
    }

    @Retryable(value = {PpcException.class}, backoff = @Backoff(delay = 2000, multiplier = 1.5))
    public int getCommitmentStatus(byte[] commitment) throws PpcException {
        try {
            return fiscoBcosClient.getCommitmentStatus(commitment);
        } catch (ContractException e) {
            log.error("Error during getCommitmentStatus: ", e);
            throw new PpcException(EnumResponseStatus.FAILURE.getErrorCode(), e.getMessage());
        }
    }

}