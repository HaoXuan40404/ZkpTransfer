package com.webank.wedpr.zktransfer.message.amop;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=false)
@Data
public class ChainDepositRequest {
    private byte[] proof;
    private byte[] commitment;
    private byte[] viewKey;
    private byte[] cipher;
    private long amount;
}