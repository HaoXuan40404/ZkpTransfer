package com.webank.ppc.iss.message;

import com.webank.ppc.iss.common.PpcCommonUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetAgencyRequest extends BaseRequest {
    private String agencyId;
    private String agencyName;
    private String agencyPublicKey;
    private String agencyDescription;
    private Boolean isComputationProvider;
    private String gatewayUrl;
    private String managementUrl;
    private String jobUrl;
    private Long dateRangeStart = PpcCommonUtils.DEFAULT_EMPTY_VALUE;
    private Long dateRangeEnd = PpcCommonUtils.DEFAULT_EMPTY_VALUE;
    private Integer pageOffset = PpcCommonUtils.DEFAULT_PAGE_OFFSET;
    private Integer pageSize = PpcCommonUtils.DEFAULT_PAGE_SIZE;
}
