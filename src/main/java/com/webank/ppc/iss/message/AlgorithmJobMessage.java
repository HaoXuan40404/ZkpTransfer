package com.webank.ppc.iss.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class AlgorithmJobMessage {
    long total;

    @JsonProperty("isLastPage")
    boolean lastPage;

    List<AlgorithmJobInfo> content;
}
