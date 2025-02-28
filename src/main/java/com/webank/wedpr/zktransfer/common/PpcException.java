package com.webank.wedpr.zktransfer.common;

import com.webank.wedpr.zktransfer.message.BaseResponse;

@SuppressWarnings("serial")
public class PpcException extends RuntimeException {
    private BaseResponse baseResponse;

    public PpcException(int errorCode, String message) {
        super(message);
        this.baseResponse = new BaseResponse(errorCode, message);
    }

    public BaseResponse getBaseResponse() {
        return baseResponse;
    }
}
