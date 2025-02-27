package com.webank.ppc.iss.entity;

import javax.persistence.*;
import lombok.Data;

/**
 * @author: caryliao
 * @date: 2022/2/18 10:38
 */
@Entity
@Data
@IdClass(DatasetAuthorizationRequestKey.class)
@Table(name = "d_authorization_request")
public class DatasetAuthorizationRequest {
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Integer id;

    private long status;

    @Id
    private String datasetId;


    private String datasetTitle;

    @Id
    private String authorizedAgencyId;


    private String ownerAgencyId;

    @Id
    private String algorithmId;

    private Long expiredTime;

    private Long createTime;

    private Long updateTime;

    private Long blockNumber;
}
