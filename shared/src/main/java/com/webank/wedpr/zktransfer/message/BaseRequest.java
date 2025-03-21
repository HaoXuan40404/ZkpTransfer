package com.webank.wedpr.zktransfer.message;

import lombok.Data;

@Data
public class BaseRequest {
    private String agencyName;
    private String agencyId;
    private String bizSeq;
}
