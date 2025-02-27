package com.webank.ppc.iss.entity;

import lombok.Data;

import javax.persistence.*;


/**
 * @author: caryliao
 * @date: 2022/2/18 10:38
 */
@Entity
@Data
@IdClass(AlgorithmKey.class)
@Table(name = "d_algorithm")
public class Algorithm {
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Integer id;

    private long status;

    private String ownerAgencyId;

    @Id
    private String algorithmId;

    @Id
    private String algorithmVersion;

    private String algorithmTitle;

    private String ownerAgencyName;

    private String algorithmType;

    private String algorithmSubtype;

    @Column(columnDefinition="text")
    private String algorithmContent;

    private Long createTime;

    private Long updateTime;

    private Long blockNumber;
}
