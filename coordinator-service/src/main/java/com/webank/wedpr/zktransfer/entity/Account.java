package com.webank.wedpr.zktransfer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Setter
@Getter
@Entity
@Table(name = "t_account", indexes = {
    @Index(name = "idx_address", columnList = "address"),
})
public class Account {

    @Id
    @Column(name = "id", nullable = false, length = 256)
    private String id;

    @Column(name = "address", nullable = false, length = 256)
    private String address;

    @Column(name = "name", nullable = false, length = 256)
    private String name;

    @Column(name = "url", nullable = false, length = 256)
    private String url;

    @Column(name = "status", nullable = false)
    private int status;

    @Column(name = "create_time", nullable = false)
    private Timestamp createTime;

    @Column(name = "update_time", nullable = false)
    private Timestamp updateTime;


    @Override
    public String toString() {
        return "Account{" +
                "id='" + id + '\'' +
                ", address='" + address + '\'' +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", status=" + status +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
