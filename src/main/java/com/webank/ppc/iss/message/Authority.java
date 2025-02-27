package com.webank.ppc.iss.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class Authority {
    @JsonProperty("authorizedAgencyId")
    private String agencyId;

    @JsonProperty("authorizationDate")
    private Long expiredTime;

    @NotBlank(message = "algorithmId list must not be empty")
    private String algorithmId;
}
