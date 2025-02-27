package com.webank.ppc.iss.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AgencyInfo {
    private String agencyId;
    private String agencyName;
    private String agencyPublicKey;
    private String agencyDescription;

    @JsonProperty("isComputationProvider")
    private Boolean computationProvider;

    private String gatewayUrl;
    private String managementUrl;
    private String jobUrl;
    private Long createTime;
    private Long updateTime;
}
