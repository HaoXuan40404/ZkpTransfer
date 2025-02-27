package com.webank.ppc.iss.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class DatasetMessage {
    long total;

    @JsonProperty("isLastPage")
    boolean lastPage;

    List<DatasetContent> content = new ArrayList<>();
}
