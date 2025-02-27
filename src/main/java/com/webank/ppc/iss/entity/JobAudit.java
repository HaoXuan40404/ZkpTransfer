package com.webank.ppc.iss.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * @author: caryliao
 * @date: 2022/2/18 10:38
 */
@Entity
@Data
@IdClass(JobAuditKey.class)
@Table(name = "d_job_audit")
public class JobAudit {
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Long id;

    private long status;

    @Id
    private String jobId;

    private String jobTitle;

    @Id
    private String agencyId;

    private String userName;

    @Id
    private String userRole;

    private String jobStatus;

    @Column(columnDefinition="varchar(1000) default ''")
    private String auditData;

    private Long createTime;

    private Long updateTime;

    private Long blockNumber;
}
