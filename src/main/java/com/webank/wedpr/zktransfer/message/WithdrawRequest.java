package com.webank.wedpr.zktransfer.message;

import lombok.Data;

@Data
public class WithdrawRequest {
    private String fromAddress;
    private long amount;
    private long timestamp;
}