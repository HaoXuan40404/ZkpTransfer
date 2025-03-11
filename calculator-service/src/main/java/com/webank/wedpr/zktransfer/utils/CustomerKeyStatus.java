package com.webank.wedpr.zktransfer.utils;

public enum CustomerKeyStatus {
    NotExist(0), // 不存在
    Exist(1),  // 未花费
    pending(2);     // 已花费

    private final int value;

    CustomerKeyStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    private static final CustomerKeyStatus DEFAULT = NotExist;

    public static CustomerKeyStatus getDefault() {
        return DEFAULT;
    }
}