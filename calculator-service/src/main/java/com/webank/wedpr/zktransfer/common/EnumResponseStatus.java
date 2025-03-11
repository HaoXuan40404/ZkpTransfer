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
    PROTO_DECODED_FAILED(20001, "decoded proto failed"),
    KILLED_JOB_FAILED(20002, "killed job failed"),
    CHECK_JOB_TYPE_FAILED(20003, "check job type failed"),
    KILLED_JOB_NOT_FOUND(20004, "killed job not found"),
    FAILURE(1, "custom error message");

    private Integer errorCode;
    private String message;
}
