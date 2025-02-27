package com.webank.ppc.iss.message.blockchain;

import lombok.Data;

import java.util.HashMap;

/**
 * @author: caryliao
 * @date: 2022/2/25 10:41
 */
@Data
public class DatasetAuthorityChainSet {
    // key authorizedAgencyId + algorithmId
    // value authorizationDate
//    private String dataKey;
    long authorizationDate;
//    private HashMap<String, Long> authorizedSet;
//    private String authorizedAgencyId;
//    private String algorithmId;
//    private Long authorizationDate;
}
