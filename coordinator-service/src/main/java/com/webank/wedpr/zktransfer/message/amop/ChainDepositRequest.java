package com.webank.wedpr.zktransfer.message.amop;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=false)
@Data
public class ChainDepositRequest {
    private byte[] commitment;
    private byte[] viewKey;
    private byte[] cipher;
}