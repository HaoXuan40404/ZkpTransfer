package com.webank.ppc.iss.message;

import com.webank.ppc.iss.common.PpcCommonUtils;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UploadAgencyRequest extends BaseRequest {
    @NotBlank(message = "agencyId must not be blank")
    private String agencyId;

    @NotBlank(message = "agencyName must not be blank")
    private String agencyName;

    @NotBlank(message = "agencyPublicKey must not be blank")
    private String agencyPublicKey;

    @NotBlank(message = "agencyDescription must not be blank")
    private String agencyDescription;

    @NotNull(message = "isComputationProvider must not be null")
    private Boolean isComputationProvider;

    private String gatewayUrl = PpcCommonUtils.DEFAULT_EMPTY_STR;

    private String managementUrl = PpcCommonUtils.DEFAULT_EMPTY_STR;

    private String jobUrl = PpcCommonUtils.DEFAULT_EMPTY_STR;
}
