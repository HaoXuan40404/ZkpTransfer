package com.webank.ppc.iss.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AuthorityRemove {
    @JsonProperty("authorizedAgencyId")
    private String agencyId;

    @NotBlank(message = "algorithmId list must not be empty")
    private String algorithmId;
}
