package com.webank.wedpr.zktransfer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webank.wedpr.zktransfer.contracts.ZkTransfer;
import com.webank.wedpr.zktransfer.message.*;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class FiscoBcosClient {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private Client client;
    @Autowired
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

    public String mint(byte[] proof, byte[] commitment, byte[] viewKey, byte[] cipher, long value) throws ContractException {
        TransactionReceipt receipt = zkTransfer.mint(proof, commitment, viewKey, cipher, BigInteger.valueOf(value));
        checkTransactionReceipt(receipt);
        return receipt.getTransactionHash();
    }

    public String transfer(List<byte[]> inputCommitments, List<byte[]> outputCommitments, List<byte[]> outputViewKeys, List<byte[]> outputNoteCiphers, byte[] relationshipProof, List<byte[]> knowledgeProofs, List<byte[]> rangeProofs) throws ContractException {
        TransactionReceipt receipt = zkTransfer.transfer(inputCommitments, outputCommitments, outputViewKeys, outputNoteCiphers, relationshipProof, knowledgeProofs, rangeProofs);
        checkTransactionReceipt(receipt);
        return receipt.getTransactionHash();
    }

    public String burn(List<byte[]> proofsList, byte[] commitment, long value, String account) throws ContractException {
        TransactionReceipt receipt = zkTransfer.burn(proofsList, commitment, BigInteger.valueOf(value), account);
        checkTransactionReceipt(receipt);
        return receipt.getTransactionHash();
    }

}
