package com.webank.wedpr.zktransfer.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetCustomerKeyResponse extends BaseResponse {
    int totalBalance;
    int availableBalance;
}
