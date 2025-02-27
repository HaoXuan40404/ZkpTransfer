package com.webank.ppc.iss.entity;

import javax.persistence.*;
import lombok.Data;

/**
 * @author: caryliao
 * @date: 2022/2/18 10:38
 */
@Entity
@Data
@IdClass(DatasetAuthorizationKey.class)
@Table(name = "d_authorization")
public class DatasetAuthorization {
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Integer id;

    private long status;

    @Id
    private String datasetId;

    @Id
    private String authorizedAgencyId;

    @Id
    private String algorithmId;

    private Long expiredTime;

    private Long createTime;

    private Long updateTime;

    private Long blockNumber;
}
