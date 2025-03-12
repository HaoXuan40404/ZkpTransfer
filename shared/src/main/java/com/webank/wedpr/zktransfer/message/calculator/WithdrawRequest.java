package com.webank.wedpr.zktransfer.message.calculator;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=false)
@Data
public class WithdrawRequest {
    private int amount;
}