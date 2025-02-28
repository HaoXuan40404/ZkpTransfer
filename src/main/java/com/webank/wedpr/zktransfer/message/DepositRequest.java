package com.webank.wedpr.zktransfer.message;

import lombok.Data;

@Data
public class DepositRequest {
    private String accountAddress;
    private long amount;
    private long timestamp;
}