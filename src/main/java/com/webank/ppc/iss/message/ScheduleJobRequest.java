package com.webank.ppc.iss.message;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ScheduleJobRequest extends BaseRequest {
    @NotNull(message = "jobId must not be blank")
    private String jobId;

    @NotBlank(message = "jobEvent must not be blank")
    private String jobEvent;
}
