package com.webank.wedpr.zktransfer.message.coordinator;

import com.webank.wedpr.zktransfer.message.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=false)
@Data
public class MintCommitmentRequest extends BaseRequest {
    private byte[] proof; // value equality proof
    private byte[] rangeProof; // for transfer
    private byte[] commitment;
    private byte[] viewKey;
    private byte[] cipher;
    private int amount;
}