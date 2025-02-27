package com.webank.ppc.iss.message;

import com.webank.ppc.iss.common.PpcCommonUtils;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateDatasetRequest extends BaseRequest {
    @NotBlank(message = "ownerAgencyId must not be blank")
    private String ownerAgencyId;

    @NotBlank(message = "datasetId must not be blank")
    private String datasetId;

    /** fields to be updated */
    private String datasetTitle = PpcCommonUtils.DEFAULT_EMPTY_STR;

    private String dataDetail = PpcCommonUtils.DEFAULT_EMPTY_STR;
}
