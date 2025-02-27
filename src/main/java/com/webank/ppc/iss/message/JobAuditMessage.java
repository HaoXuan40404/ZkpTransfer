package com.webank.ppc.iss.message;

import java.util.List;
import lombok.Data;

@Data
public class JobAuditMessage {
    String jobId;
    String jobTitle;
    List<JobAuditInfo> jobAuditInfoList;
}
