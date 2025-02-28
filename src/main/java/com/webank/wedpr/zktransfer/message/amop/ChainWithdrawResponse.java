package com.webank.wedpr.zktransfer.message.amop;

import com.webank.wedpr.zktransfer.message.BaseResponse;

import lombok.Data;

@Data
public class ChainWithdrawResponse extends BaseResponse {
    private String txHash;
    private long blockNumber;
    private String status;
}