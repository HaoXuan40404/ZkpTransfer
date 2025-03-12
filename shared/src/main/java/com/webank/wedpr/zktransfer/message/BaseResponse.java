package com.webank.wedpr.zktransfer.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponse {
    protected Integer errorCode;
    protected String message;
}
