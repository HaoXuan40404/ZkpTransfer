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
//@IdClass(Agency.class)
@Table(name = "d_agency")
public class Agency {
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Integer id;

    @Id
    private String agencyId;

    private long status;

    private String agencyName;

    @Column(columnDefinition="varchar(200) default ''")
    private String agencyPublicKey;

    @Column(columnDefinition="varchar(500) default ''")
    private String agencyDescription;

    private Boolean isComputationProvider;

    private String gatewayUrl;

    private String managementUrl;

    private String jobUrl;

    private Long createTime;

    private Long updateTime;

    private Long blockNumber;
}
