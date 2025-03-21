package com.webank.wedpr.zktransfer.message.coordinator;


import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=false)
@Data
public class TransferStatusUpdateRequest {
    private byte[] commitment;
}
