package com.webank.wedpr.zktransfer.message.calculator;

import java.util.List;

import com.webank.wedpr.zktransfer.message.coordinator.TransferCommitmentRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=false)
@Data
public class TransferRecord {
    private String role;
    private List<byte[]> senderBlinding;
    private byte[] receiverBlinding;
    private TransferCommitmentRequest senderRequest;
    private TransferReceiveResponse receiverResponse;
}