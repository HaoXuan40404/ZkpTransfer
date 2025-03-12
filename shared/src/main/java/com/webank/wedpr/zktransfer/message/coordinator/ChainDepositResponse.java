package com.webank.wedpr.zktransfer.message.coordinator;

import com.webank.wedpr.zktransfer.message.BaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=false)
@Data
public class ChainDepositResponse extends BaseResponse {
    private String txHash;
    private long blockNumber;
    private String status;
}