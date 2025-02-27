package com.webank.ppc.iss.message;

import com.webank.ppc.iss.common.PpcCommonUtils;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateAgencyRequest extends BaseRequest {
    @NotBlank(message = "agencyId must not be blank")
    private String agencyId;

    private String agencyName = PpcCommonUtils.DEFAULT_EMPTY_STR;

    private String agencyPublicKey = PpcCommonUtils.DEFAULT_EMPTY_STR;

    private String agencyDescription = PpcCommonUtils.DEFAULT_EMPTY_STR;

    @NotNull(message = "isComputationProvider must not be null")
    private Boolean isComputationProvider;

    private String gatewayUrl = PpcCommonUtils.DEFAULT_EMPTY_STR;

    private String managementUrl = PpcCommonUtils.DEFAULT_EMPTY_STR;

    private String jobUrl = PpcCommonUtils.DEFAULT_EMPTY_STR;
}
