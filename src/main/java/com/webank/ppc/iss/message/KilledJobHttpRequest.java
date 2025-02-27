package com.webank.ppc.iss.message;

import lombok.Data;

@Data
public class KilledJobHttpRequest {
    private String jobId;
    private String jobAlgorithmType;
}
