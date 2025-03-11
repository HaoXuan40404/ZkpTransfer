package com.webank.wedpr.zktransfer.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StatusEnum {
    Normal(0),
    Removed(1);
    private long value;
}
