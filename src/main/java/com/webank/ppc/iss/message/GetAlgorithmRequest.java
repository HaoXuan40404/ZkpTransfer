package com.webank.ppc.iss.message;

import com.webank.ppc.iss.common.PpcCommonUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetAlgorithmRequest extends BaseRequest {
    private String ownerAgencyId = "";
    private String algorithmId = "";
    private String algorithmVersion = "";
    private String algorithmTitle = "";
    private String ownerAgencyName = "";
    private String algorithmType = "";
    private String algorithmSubtype = "";
    private Long dateRangeStart = PpcCommonUtils.DEFAULT_EMPTY_VALUE;
    private Long dateRangeEnd = PpcCommonUtils.DEFAULT_EMPTY_VALUE;
    private Integer pageOffset = PpcCommonUtils.DEFAULT_PAGE_OFFSET;
    private Integer pageSize = PpcCommonUtils.DEFAULT_PAGE_SIZE;
}
