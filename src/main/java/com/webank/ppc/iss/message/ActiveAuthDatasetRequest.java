package com.webank.ppc.iss.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ActiveAuthDatasetRequest extends BaseRequest {
    @NotBlank(message = "datasetId must not be blank")
    private String datasetId;

    List<Authority> authorizedAlgoList;

//    @JsonProperty("authorizedAgencyIdList")
//    private List<String> agencyIdList;
//
//    @JsonProperty("authorizationDateList")
//    private List<Long> expiredTimeList;
//
//    @NotEmpty(message = "algorithmId list must not be empty")
//    private List<String> algorithmIdList;


}
