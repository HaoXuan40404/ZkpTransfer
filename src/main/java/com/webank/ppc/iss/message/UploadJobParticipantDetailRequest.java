package com.webank.ppc.iss.message;

import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UploadJobParticipantDetailRequest extends BaseRequest {
    @NotBlank(message = "jobId must not be blank")
    private String jobId;

    @NotBlank(message = "algorithmId must not be blank")
    private String algorithmId;

    @NotNull(message = "datasetIdList must not be null")
    private List<String> datasetIdList;
}
