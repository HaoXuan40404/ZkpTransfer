package com.webank.ppc.iss.message;

import lombok.Data;

/**
 * @author: caryliao
 * @date: 2022/2/25 10:41
 */
@Data
public class DatasetAuthority {
    private String authorizedAgencyId;
    private String algorithmId;
    private Long authorizationDate;
}
