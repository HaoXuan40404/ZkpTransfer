package com.webank.wedpr.zktransfer.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=false)
@Data
public class WithdrawRequest {
    private String fromAddress;
    private long amount;
    private long timestamp;
}