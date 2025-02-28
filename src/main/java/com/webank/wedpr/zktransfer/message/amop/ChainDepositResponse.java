package com.webank.wedpr.zktransfer.message.amop;

import lombok.Data;
import com.webank.wedpr.zktransfer.message.BaseResponse;

@Data
public class ChainDepositResponse extends BaseResponse {
    private String txHash;
    private long blockNumber;
    private String status;
}