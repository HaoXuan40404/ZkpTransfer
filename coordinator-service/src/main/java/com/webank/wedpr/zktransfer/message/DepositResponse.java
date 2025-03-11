package com.webank.wedpr.zktransfer.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class DepositResponse extends BaseResponse {
    private String txHash;
    private long blockNumber;
    private String status;
}