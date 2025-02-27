package com.webank.ppc.iss.message.blockchain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlgorithmJobChainSet {
    String jobId;
}
