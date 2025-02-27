package com.webank.ppc.iss.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author: caryliao
 * @date: 2022/2/18 10:38
 */
@Entity
@Data
//@IdClass(Dataset.class)
@Table(name = "d_dataset")
public class Dataset {
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Integer id;

    private long status;

    private String ownerAgencyId;

    @Id
    private String datasetId;

    private String ownerAgencyName;

    private String datasetTitle;

    private String datasetAlgorithm;

    @Column(columnDefinition="text")
    private String dataDetail;

    private Long createTime;

    private Long updateTime;

    private String algorithmFlag;

    private Long blockNumber;
}
