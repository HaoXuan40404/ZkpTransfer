package com.webank.ppc.iss.message;

import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UploadJobAuditRequest extends BaseRequest {
    @NotBlank(message = "jobId must not be blank")
    private String jobId;

    //    @NotBlank(message = "jobTitle must not be blank")
    private String jobTitle;

    @NotBlank(message = "agencyId must not be blank")
    private String agencyId;

    //    @NotBlank(message = "userName must not be blank")
    private String userName;

    @NotBlank(message = "userRole must not be blank")
    private String userRole;

    //    @NotBlank(message = "jobStatus must not be blank")
    private String jobStatus;

    //    @NotBlank(message = "auditData must not be blank")
    private String auditData;
}
