package com.webank.ppc.iss.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "t_job_detail")
public class JobDetailEntity {
    @Id
    private String jobId;
    private String jobTitle;
    private String jobDescription;
    private Integer jobPriority;
    private String jobCreator;
    private String tagProviderAgencyId;
    private Boolean enableShareResult;
    private String initiatorAgencyId;
    private String initiatorAgencyName;
    private String participateAgencyId;
    private String jobAlgorithmId;
    private String jobAlgorithmTitle;
    private String jobAlgorithmVersion;
    private String jobAlgorithmType;
    private String jobAlgorithmAgencyId;
    private String jobAlgorithmAgencyName;
    private String jobAlgorithmSubtype;
    private String jobModelType;
    private String jobStatus;
    private String jobComputationProviderListPb;
    private String jobDatasetProviderListPb;
    private String jobResultReceiverListPb;
    private Long createTime;
    private Double elapsedTime;
    private Double trafficVolume;
    private String outputPreview;
    private String psiFields;
    private String matchFieldsStr;
}