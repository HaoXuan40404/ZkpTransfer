package com.webank.wedpr.zktransfer.message.coordinator;

import com.webank.wedpr.zktransfer.message.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=false)
@Data
public class ChainDepositRequest extends BaseRequest {
    private byte[] proof;
    private byte[] commitment;
    private byte[] viewKey;
    private byte[] cipher;
    private int amount;
}