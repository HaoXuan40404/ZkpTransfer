package com.webank.wedpr.zktransfer.message.coordinator;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=false)
@Data
public class RegisterInfoRequest {
    private String id;
    private String address;
    private String name;
    private String url;
}
