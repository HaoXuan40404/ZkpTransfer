package com.webank.wedpr.zktransfer.message.amop;

import lombok.Data;

import java.util.List;

@Data
public class ChainTransferRequest {
    private List<String> inputCommitments;
    private List<String> outputCommitments;
    private List<String> outputViewKeys;
    private List<String> outputNoteCiphers;
    private String relationshipProof;
    private List<String> knowledgeProofs;
    private List<String> rangeProofs;
}