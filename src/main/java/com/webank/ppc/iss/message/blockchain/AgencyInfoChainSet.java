package com.webank.ppc.iss.message.blockchain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AgencyInfoChainSet {
    private String agencyId;
    private String agencyName;
    private String agencyPublicKey;
    private String agencyDescription;

//    @JacksonInject("")
//    @JsonProperty("isComputationProvider")
    private Boolean computationProvider;

//    @JacksonInject("")
    private String gatewayUrl;

//    @JacksonInject("")
    private String managementUrl;

//    @JacksonInject("")
    private String jobUrl;
    private Long createTime;

//    @JacksonInject("")
    private Long updateTime;
}
