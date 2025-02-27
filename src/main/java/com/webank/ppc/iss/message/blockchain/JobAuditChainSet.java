package com.webank.ppc.iss.message.blockchain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobAuditChainSet {
    String agencyId;
    String userName;
    String userRole;
    String jobStatus;
    String auditData;
    String jobTitle;
    long createTime;
    long updateTime;
}
