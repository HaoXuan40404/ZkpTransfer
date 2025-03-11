package com.webank.wedpr.zktransfer.message.amop;

import com.webank.wedpr.zktransfer.message.BaseResponse;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=false)
@Data
public class ChainTransferResponse extends BaseResponse {
    private String txHash;
    private long blockNumber;
    private String status;
}