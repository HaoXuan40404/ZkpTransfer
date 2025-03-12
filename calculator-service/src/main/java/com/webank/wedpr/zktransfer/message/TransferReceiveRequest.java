package com.webank.wedpr.zktransfer.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=false)
@Data
public class TransferReceiveRequest extends  BaseRequest{
    private String fromBankInfo;
    private int receiveAmount;
}