package com.webank.wedpr.zktransfer.message;

import lombok.Data;

@Data
public class TransferResponse extends BaseResponse {
    private String txHash;
    private long blockNumber;
    private String status;
}