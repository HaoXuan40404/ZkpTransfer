package com.webank.ppc.iss.message;

import com.webank.ppc.iss.common.PpcCommonUtils;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetDatasetJobRequest extends BaseRequest {
    @NotBlank(message = "datasetId must not be blank")
    private String datasetId;

    private String jobId = "";
    private Integer pageOffset = PpcCommonUtils.DEFAULT_PAGE_OFFSET;
    private Integer pageSize = PpcCommonUtils.DEFAULT_PAGE_SIZE;
}
