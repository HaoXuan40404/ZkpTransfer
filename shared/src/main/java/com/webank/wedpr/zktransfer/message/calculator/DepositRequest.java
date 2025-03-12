package com.webank.wedpr.zktransfer.message.calculator;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class DepositRequest {
    private int amount;
}