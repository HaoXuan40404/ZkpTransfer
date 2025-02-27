package com.webank.ppc.iss.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ActiveAuthDatasetByOtherRequest extends BaseRequest {
    @NotBlank(message = "datasetId must not be blank")
    private String datasetId;

    List<Authority> authorizedAlgoList;
//
//
//    @NotBlank(message = "agencyId must not be blank")
//    private String agencyId;
//
//    @NotBlank(message = "algorithmId must not be blank")
//    private String algorithmId;
//
//    @JsonProperty("authorizedAgencyIdList")
//    private List<String> agencyIdList;
//
//    @JsonProperty("authorizationDateList")
//    private List<Long> expiredTimeList;
//
//    private List<String> algorithmIdList;
}
