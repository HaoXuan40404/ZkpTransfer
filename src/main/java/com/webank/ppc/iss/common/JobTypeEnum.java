package com.webank.ppc.iss.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author asher
 * @date 2024/6/24
 */
@AllArgsConstructor
@Getter
public enum JobTypeEnum {
    CREATE("CREATE"),
    FAILED("FAILED"),
    KILLED("KILLED");
    private String value;
}
