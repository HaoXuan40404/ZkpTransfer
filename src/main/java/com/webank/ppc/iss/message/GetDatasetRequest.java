package com.webank.ppc.iss.message;

import com.webank.ppc.iss.common.PpcCommonUtils;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetDatasetRequest extends BaseRequest {
    @NotBlank(message = "authorizeAgencyId must not be blank")
    private String authorizeAgencyId;

    private String ownerAgencyId = "";
    private String ownerAgencyName = "";
    private String datasetId = "";
    private String datasetTitle = "";
    private String algorithmId = "";
    private String datasetAlgorithm = "";
    private Boolean showAvailable = false;
    private Long dateRangeStart = PpcCommonUtils.DEFAULT_EMPTY_VALUE;
    private Long dateRangeEnd = PpcCommonUtils.DEFAULT_EMPTY_VALUE;
    private Integer pageOffset = PpcCommonUtils.DEFAULT_PAGE_OFFSET;
    private Integer pageSize = PpcCommonUtils.DEFAULT_PAGE_SIZE;
    //    private Long isAysPreprocessing = PpcCommonUtils.DEFAULT_EMPTY_VALUE;
    private String algorithmFlag = "";
}
