package com.webank.wedpr.zktransfer.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class BalanceResponse extends BaseResponse {
    private long totalBalance;
    private long availableBalance;
    private long lockedBalance;
}