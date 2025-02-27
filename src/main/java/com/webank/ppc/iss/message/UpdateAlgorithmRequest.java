package com.webank.ppc.iss.message;

import com.webank.ppc.iss.common.PpcCommonUtils;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateAlgorithmRequest extends BaseRequest {
    @NotBlank(message = "ownerAgencyId must not be blank")
    private String ownerAgencyId;

    @NotBlank(message = "algorithmId must not be blank")
    private String algorithmId;

    @NotBlank(message = "algorithmVersion must not be blank")
    private String algorithmVersion;

    /** fields to be updated */
    private String algorithmTitle = PpcCommonUtils.DEFAULT_EMPTY_STR;

    private String algorithmContent = PpcCommonUtils.DEFAULT_EMPTY_STR;
}
