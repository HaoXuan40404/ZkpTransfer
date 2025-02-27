package com.webank.ppc.iss.message;

import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UploadAlgorithmRequest extends BaseRequest {
    @NotBlank(message = "ownerAgencyId must not be blank")
    private String ownerAgencyId;

    @NotBlank(message = "algorithmId must not be blank")
    private String algorithmId;

    @NotBlank(message = "algorithmVersion must not be blank")
    private String algorithmVersion;

    @NotBlank(message = "algorithmTitle must not be blank")
    private String algorithmTitle;

    @NotBlank(message = "ownerAgencyName must not be blank")
    private String ownerAgencyName;

    @NotBlank(message = "algorithmType must not be blank")
    private String algorithmType;

    private String algorithmSubtype;

    @NotBlank(message = "algorithmContent must not be blank")
    private String algorithmContent;
}
