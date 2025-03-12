package com.webank.wedpr.zktransfer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.io.Serializable;
import java.sql.Timestamp;

@Setter
@Getter
@Entity
@Table(name = "t_commitment")
public class CommitmentEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "commitment", length = 256, nullable = false)
    private String commitment;

    @Column(name = "kdf_index", nullable = false, unique = true)
    private int kdfIndex;

    @Column(name = "commitment_value", nullable = false)
    private int commitmentValue;

    @Column(nullable = false)
    private int status;

    @Column(name = "create_time", nullable = false)
    private Timestamp createTime;

    @Column(name = "update_time", nullable = false)
    private Timestamp updateTime;

    @Override
    public String toString() {
        return "CommitmentEntity{" +
               "commitment='" + commitment + '\'' +
               ", index='" + kdfIndex + '\'' +
               ", value=" + commitmentValue +
               ", createTime=" + createTime +
               ", updateTime=" + updateTime +
               '}';
    }
}
