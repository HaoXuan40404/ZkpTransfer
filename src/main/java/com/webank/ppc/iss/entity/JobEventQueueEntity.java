package com.webank.ppc.iss.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "t_job_event_queue")
public class JobEventQueueEntity {
    @Id
    private String jobId;
    @Column(columnDefinition = "text")
    private String jobEvent;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
}