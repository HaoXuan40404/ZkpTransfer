package com.webank.ppc.iss.message;

import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DeleteAgencyRequest extends BaseRequest {
    @NotBlank(message = "agencyId must not be blank")
    private String agencyId;
}
