package com.webank.wedpr.zktransfer.message.coordinator;


import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=false)
@Data
public class TransferStatusUpdateResponse {
    private byte[] commitment;
}
