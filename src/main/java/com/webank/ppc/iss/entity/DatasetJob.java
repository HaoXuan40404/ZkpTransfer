package com.webank.ppc.iss.entity;

import javax.persistence.*;
import lombok.Data;

@Entity
@Data
@IdClass(DatasetJobKey.class)
@Table(name = "d_dataset_job")
public class DatasetJob {
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Integer id;

    private long status;

    @Id
    private String datasetId;

    @Id
    private String jobId;

    private Long blockNumber;
}
