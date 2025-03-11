package com.webank.wedpr.zktransfer.message.amop;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper=false)
@Data
public class ChainTransferRequest {
    private List<byte[]> inputCommitments;
    private List<byte[]> outputCommitments;
    private List<byte[]> outputViewKeys;
    private List<byte[]> outputNoteCiphers;
    private byte[] relationshipProof;
    private List<byte[]> knowledgeProofs;
    private List<byte[]> rangeProofs;
}