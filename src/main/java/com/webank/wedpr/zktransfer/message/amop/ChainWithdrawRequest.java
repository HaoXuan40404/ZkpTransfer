package com.webank.wedpr.zktransfer.message.amop;

import lombok.Data;

@Data
public class ChainWithdrawRequest {
    private String proof;
    private String commitment;
    private long amount;
    private String account;
}