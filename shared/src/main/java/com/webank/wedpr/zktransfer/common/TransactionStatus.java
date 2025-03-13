package com.webank.wedpr.zktransfer.common;

public enum TransactionStatus {
    Undefined(0),  // 未定义
    Deposit(1),    // 入金
    Withdraw(2),   // 出金
    TransferOut(3), // 转账花费
    TransferIn(4);  // 转账收款

    private final int value;

    TransactionStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    private static final TransactionStatus DEFAULT = Undefined;

    public static TransactionStatus getDefault() {
        return DEFAULT;
    }
}