package com.webank.wedpr.zktransfer.message.amop;

import lombok.Data;

@Data
public class ChainDepositRequest {
    private String proof;
    private String commitment;
    private String viewKey;
    private String cipher;
    private long amount;
}