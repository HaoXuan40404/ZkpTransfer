package com.webank.ppc.iss.message.blockchain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlgorithmInfoChainSet {
    String ownerAgencyId;
    String algorithmId;
    String algorithmVersion;
    String algorithmTitle;
    String ownerAgencyName;
    String algorithmContent;
    String algorithmType;
    String algorithmSubtype;
    Long createTime;
    Long updateTime;

}
