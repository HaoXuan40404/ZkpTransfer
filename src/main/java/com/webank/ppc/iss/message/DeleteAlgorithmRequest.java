package com.webank.ppc.iss.message;

import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DeleteAlgorithmRequest extends BaseRequest {
    @NotBlank(message = "ownerAgencyId must not be blank")
    private String ownerAgencyId;

    @NotBlank(message = "algorithmId must not be blank")
    private String algorithmId;

    private String algorithmVersion = "";
}
