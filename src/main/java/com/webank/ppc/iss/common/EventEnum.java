package com.webank.ppc.iss.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: caryliao
 * @date: 2022/3/22 14:50
 */
@AllArgsConstructor
@Getter
public enum EventEnum {
    UPLOAD("UPLOAD"),
    UPDATE("UPDATE"),
    REMOVE("REMOVE"),
    JOB("JOB");
    private String value;
}
