package com.webank.wedpr.zktransfer.message;

import lombok.Data;

@Data
public class BalanceResponse extends BaseResponse {
    private long totalBalance;
    private long availableBalance;
    private long lockedBalance;
}