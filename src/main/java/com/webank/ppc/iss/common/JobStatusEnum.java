package com.webank.ppc.iss.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author asher
 * @date 2024/6/25
 */
@AllArgsConstructor
@Getter
public enum JobStatusEnum {
    SUCCEED(1),
    FAILED(2),
    RUNNING(3),
    WAITING(4),
    NONE(5),
    RETRY(6),
    KILLED(6);
    private int value;
}
