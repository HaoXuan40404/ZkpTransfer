package com.webank.ppc.iss.message;

import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DeleteAuthDatasetRequest extends BaseRequest {
    @NotBlank(message = "datasetId must not be blank")
    private String datasetId;

    List<AuthorityRemove> authorityRemoveList;

//    @NotEmpty(message = "agencyId list must not be empty")
//    private List<String> agencyIdList;
//
//    @NotEmpty(message = "algorithmId list must not be empty")
//    private List<String> algorithmIdList;
}
