package com.webank.wedpr.zktransfer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webank.wedpr.zktransfer.contracts.ZkTransfer;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;
import org.springframework.beans.factory.annotation.Autowired;

public class FiscoBcosClient {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private Client client;

//    @Autowired
    // private CryptoConfig cryptoConfig;

    private final ZkTransfer zkTransfer;

    public FiscoBcosClient(ZkTransfer _zkTransfer) {
        this.zkTransfer = _zkTransfer;
    }

    public byte[] getCipherByViewKey(byte[] viewKey) throws ContractException {
        return zkTransfer.queryNoteSetCipherByKey(viewKey);
    }

    public int getCommitmentStatus(byte[] commitment) throws ContractException {
        return zkTransfer.queryCommitmentStatus(commitment).intValue();
    }
}
