package com.webank.ppc.iss.message.blockchain;

//import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DatasetInfoChainSet {
    private String ownerAgencyId;
    private String ownerAgencyName;
    private String datasetId;
    private String datasetTitle;
    private String datasetAlgorithm;
    private String dataDetail;
    private Long createTime;

//    @JsonSetter(nulls = Nulls.SKIP)
    private Long updateTime;

//    @JsonSetter(nulls = Nulls.SKIP)
    private String algorithmFlag;
}
