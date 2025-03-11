package com.webank.wedpr.zktransfer.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=false)
@Data
public class TransferRequest {
    private String from;
    private String to;
    private String coordinator;
    private long amount;
    private int inputCount;
    private int outputCount;
}