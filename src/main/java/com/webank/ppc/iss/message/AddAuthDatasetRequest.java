package com.webank.ppc.iss.message;

import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AddAuthDatasetRequest extends BaseRequest {
    @NotBlank(message = "datasetId must not be blank")
    private String datasetId;

    List<Authority> authorizedAlgoList;
}
