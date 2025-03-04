package com.webank.wedpr.zktransfer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Data
@Table(name = "t_{account_address}_unspent_commitment")
public class UnspentCommitment {

    @Id
    @Column(name = "id", nullable = false, length = 256)
    private String id;

    @Column(name = "index", nullable = false, length = 32)
    private String index;

    @Column(name = "value", nullable = false)
    private int value;

    @Column(name = "block_number", nullable = false)
    private int blockNumber;

    @Column(name = "status", nullable = false)
    private int status;

    @Column(name = "create_time", nullable = false)
    private Timestamp createTime;

    @Column(name = "update_time", nullable = false)
    private Timestamp updateTime;
}