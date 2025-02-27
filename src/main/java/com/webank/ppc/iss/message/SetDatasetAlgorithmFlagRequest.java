package com.webank.ppc.iss.message;

import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SetDatasetAlgorithmFlagRequest extends BaseRequest {
    @NotBlank(message = "ownerAgencyId must not be blank")
    private String ownerAgencyId;

    @NotBlank(message = "datasetId must not be blank")
    private String datasetId;

    @NotBlank(message = "flag must not be null")
    private String flag;
}
