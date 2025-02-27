package com.webank.ppc.iss.message;

import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetJobAuditRequest extends BaseRequest {
    @NotBlank(message = "jobId must not be blank")
    private String jobId;

    private String agencyId = "";
}
