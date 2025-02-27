package com.webank.ppc.iss.message;

import com.webank.ppc.iss.common.PpcCommonUtils;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetAlgorithmJobRequest extends BaseRequest {
    @NotBlank(message = "algorithmId must not be blank")
    private String algorithmId;

    private String jobId = "";
    private Integer pageOffset = PpcCommonUtils.DEFAULT_PAGE_OFFSET;
    private Integer pageSize = PpcCommonUtils.DEFAULT_PAGE_SIZE;
}
