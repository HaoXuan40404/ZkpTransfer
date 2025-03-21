package com.webank.wedpr.zktransfer.common;

public enum CommitmentStatus {
    NotExist(0), // 不存在
    Unspent(1),  // 未花费
    Spent(2),     // 已花费
    Pending(3);     // 处理中s

    private final int value;

    CommitmentStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    private static final CommitmentStatus DEFAULT = NotExist;

    public static CommitmentStatus getDefault() {
        return DEFAULT;
    }
}