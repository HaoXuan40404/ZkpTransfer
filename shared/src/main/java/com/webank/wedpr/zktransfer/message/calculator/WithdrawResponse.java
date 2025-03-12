package com.webank.wedpr.zktransfer.message.calculator;

import com.webank.wedpr.zktransfer.message.BaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=false)
@Data
public class WithdrawResponse extends BaseResponse {
    private String txHash;
    private long blockNumber;
    private String status;
}