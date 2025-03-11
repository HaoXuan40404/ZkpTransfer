package com.webank.wedpr.zktransfer.utils;

import java.util.HexFormat;

public class Commitment {
    private CommitmentStatus status;
    private int value;
    private byte[] commitmentBytes;

    public Commitment(CommitmentStatus status, int value, byte[] commitmentBytes) {
        this.status = status;
        this.value = value;
        this.commitmentBytes = commitmentBytes;
    }

    public CommitmentStatus getStatus() {
        return status;
    }

    public int getValue() {
        return value;
    }

    public byte[] getCommitmentBytes() {
        return commitmentBytes;
    }

    @Override
    public String toString() {
        return "Commitment{" +
                "status=" + status +
                ", value='" + value + '\'' +
                ", commitmentBytes=" + HexFormat.of().formatHex(commitmentBytes) +
                '}';
    }
}