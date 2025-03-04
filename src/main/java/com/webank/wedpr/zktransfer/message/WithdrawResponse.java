package com.webank.wedpr.zktransfer.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=false)
@Data
public class WithdrawResponse extends BaseResponse {
    private String txHash;
    private long blockNumber;
    private String status;
}