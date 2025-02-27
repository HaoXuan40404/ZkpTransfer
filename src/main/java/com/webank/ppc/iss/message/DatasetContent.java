package com.webank.ppc.iss.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class DatasetContent {
    private String ownerAgencyId;
    private String ownerAgencyName;
    private String datasetId;
    private String datasetTitle;
    private String datasetAlgorithm;
    private String dataDetail;

    @JsonProperty("isAuthorized")
    private Boolean authorized;

    private List<DatasetAuthority> datasetAuthority;
    private List<DatasetAuthority> datasetAuthorityRequest;
    private Long createTime;
    private Long updateTime;

    private String algorithmFlag;
}
