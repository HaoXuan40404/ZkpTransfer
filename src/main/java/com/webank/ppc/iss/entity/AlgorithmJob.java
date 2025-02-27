package com.webank.ppc.iss.entity;

import javax.persistence.*;
import lombok.Data;

@Entity
@Data
@IdClass(AlgorithmJobKey.class)
@Table(name = "d_algorithm_job")
public class AlgorithmJob {
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Integer id;

    private long status;

    @Id
    private String algorithmId;

    @Id
    private String jobId;

    private Long blockNumber;
}
