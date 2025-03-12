package com.webank.wedpr.zktransfer.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=false)
@Data
public class TransferCompleteRequest extends  BaseRequest{
    private String check;
}