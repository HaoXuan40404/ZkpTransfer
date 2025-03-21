package com.webank.wedpr.zktransfer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Setter
@Getter
@Entity
@Table(name = "t_transaction_history")
public class TransactionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "biz_seq", nullable = false, length = 64)
    private String bizSeq;

    @Column(name = "commitment", length = 256)
    private String commitment;

    @Column(name = "value", nullable = false)
    private int value;

    @Column(name = "owner", nullable = false, length = 100)
    private String owner;

    @Column(name = "trans_type", nullable = false)
    private int transType;

    @Column(name = "create_time", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createTime;

    @Override
    public String toString() {
        return "TransactionHistory{" +
                "id=" + id +
                ", bizSeq='" + bizSeq + '\'' +
                ", commitment='" + commitment + '\'' +
                ", value=" + value +
                ", owner='" + owner + '\'' +
                ", transType=" + transType +
                ", createTime=" + createTime +
                '}';
    }
}