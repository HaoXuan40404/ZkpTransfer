package com.webank.ppc.iss.message;

import lombok.Data;

@Data
public class UploadJobEventHttpRequest {
    private String jobId;
    private String jobEvent;
}
