package com.webank.ppc.iss.message;

import lombok.Data;

@Data
public class JobAuditInfo {
    String agencyId;
    String userName;
    String userRole;
    String jobStatus;
    String auditData;
    long createTime;
    long updateTime;
}
