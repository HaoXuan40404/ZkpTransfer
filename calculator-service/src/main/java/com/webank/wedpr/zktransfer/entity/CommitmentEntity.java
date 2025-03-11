package com.webank.wedpr.zktransfer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "t_commitment_entity")
public class CommitmentEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "commitment", length = 256, nullable = false)
    private String commitment;

    @Column(nullable = false, unique = true)
    private int index;

    @Column(nullable = false)
    private int value;

    @Column(nullable = false)
    private int status;

    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false)
    private Timestamp createTime;

    @UpdateTimestamp
    @Column(name = "update_time", nullable = false)
    private Timestamp updateTime;

    @Override
    public String toString() {
        return "CommitmentEntity{" +
               "commitment='" + commitment + '\'' +
               ", index='" + index + '\'' +
               ", value=" + value +
               ", createTime=" + createTime +
               ", updateTime=" + updateTime +
               '}';
    }
}
