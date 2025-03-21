package com.webank.wedpr.zktransfer.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by caryliao on 2020/8/7 11:33
 */
@AllArgsConstructor
@Getter
public enum EnumResponseStatus {
    SUCCESS(0, "success"),
    FAILURE(1, "custom error message");

    private Integer errorCode;
    private String message;
}
