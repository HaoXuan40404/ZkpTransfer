package com.webank.ppc.iss.message;

import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UploadDatasetRequest extends BaseRequest {
    @NotBlank(message = "ownerAgencyId must not be blank")
    private String ownerAgencyId;

    @NotBlank(message = "datasetId must not be blank")
    private String datasetId;

    @NotBlank(message = "datasetTitle must not be blank")
    private String datasetTitle;

    @NotBlank(message = "ownerAgencyName must not be blank")
    private String ownerAgencyName;

    @NotBlank(message = "datasetAlgorithm must not be blank")
    // private String isPirDataset;
    private String datasetAlgorithm;

    @NotBlank(message = "dataDetail must not be blank")
    private String dataDetail;
}
