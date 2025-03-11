package com.webank.wedpr.zktransfer.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: caryliao
 * @date: 2022/3/22 14:50
 */
@AllArgsConstructor
@Getter
public enum StatusEnum {
    Normal(0),
    Removed(1);
    private long value;
}
