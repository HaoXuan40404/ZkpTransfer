package com.webank.wedpr.zktransfer.message.coordinator;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.sql.Timestamp;

@EqualsAndHashCode(callSuper=false)
@Data
public class TransactionHistoryData {
    private Long id;
    private String bizSeq;
    private String commitment;
    private int value;
    private String owner;
    private int transType;
    private Timestamp createTime;

    public TransactionHistoryData(Long id, String bizSeq, String commitment, int value, String owner, int transType, Timestamp createTime) {
        this.id = id;
        this.bizSeq = bizSeq;
        this.commitment = commitment;
        this.value = value;
        this.owner = owner;
        this.transType = transType;
        this.createTime = createTime;
    }

    public TransactionHistoryData(String bizSeq2, String owner2, int value2, String commitment2, Timestamp createTime2,
            int transType2) {
        this.bizSeq = bizSeq2;
        this.commitment = commitment2;
        this.owner = owner2;
        this.value = value2;
        this.createTime = createTime2;
        this.transType = transType2;
    }
}