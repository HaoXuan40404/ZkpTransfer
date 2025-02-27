package com.webank.ppc.iss.message;

import lombok.Data;

@Data
public class AlgorithmInfo {
    String ownerAgencyId;
    String algorithmId;
    String algorithmVersion;
    String algorithmTitle;
    String ownerAgencyName;
    String algorithmContent;
    Long createTime;
    Long updateTime;
}
