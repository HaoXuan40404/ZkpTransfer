package com.webank.ppc.iss.message;

import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UploadDatasetAuthRequest extends BaseRequest {
    @NotBlank(message = "datasetId must not be blank")
    private String datasetId;

    @NotBlank(message = "requesterAgencyId must not be blank")
    private String requesterAgencyId;

    @NotBlank(message = "jobId must not be blank")
    private String jobId;

    @NotBlank(message = "authData must not be blank")
    private String authData;
}
