package com.webank.ppc.iss.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class DatasetJobMessage {
    long total;

    @JsonProperty("isLastPage")
    boolean lastPage;

    List<DatasetJobInfo> content;
}
