package com.webank.ppc.iss.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetJobAuditResponse extends BaseResponse {
    JobAuditMessage data;
}
