package com.webank.wedpr.zktransfer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webank.wedpr.zktransfer.contracts.ZkTransfer;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

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

    private static boolean isTransactionSucceeded(TransactionReceipt transactionReceipt) {
        return 0 == transactionReceipt.getStatus();
    }

    private static void checkTransactionReceipt(TransactionReceipt transactionReceipt)
            throws ContractException {
        if (!isTransactionSucceeded(transactionReceipt)) {
            throw new ContractException(
                    transactionReceipt.getStatus() + transactionReceipt.getMessage());
        }
    }

    public String mint(byte[] commitment, byte[] viewKey, byte[] cipher) throws ContractException {
        TransactionReceipt receipt = zkTransfer.mint(commitment, viewKey, cipher);
        checkTransactionReceipt(receipt);
        return receipt.getTransactionHash();
    }

    public String transfer(List<byte[]> inputCommitments, List<byte[]> outputCommitments, List<byte[]> outputViewKeys, List<byte[]> outputNoteCiphers, byte[] relationshipProof, List<byte[]> knowledgeProofs, List<byte[]> rangeProofs) throws ContractException {
        TransactionReceipt receipt = zkTransfer.transfer(inputCommitments, outputCommitments, outputViewKeys, outputNoteCiphers, relationshipProof, knowledgeProofs, rangeProofs);
        checkTransactionReceipt(receipt);
        return receipt.getTransactionHash();
    }

    public String burn(byte[] proof, byte[] commitment) throws ContractException {
        TransactionReceipt receipt = zkTransfer.burn(proof, commitment);
        checkTransactionReceipt(receipt);
        return receipt.getTransactionHash();
    }
}
