package com.webank.ppc.iss.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "shedlock")
public class ShedLockEntity {
    @Id
    private String name;
    private LocalDateTime lockUntil;
    private LocalDateTime lockedAt;
    private String lockedBy;
}