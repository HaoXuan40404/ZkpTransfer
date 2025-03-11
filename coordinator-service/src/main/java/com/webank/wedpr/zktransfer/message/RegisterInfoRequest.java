package com.webank.wedpr.zktransfer.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RegisterInfoRequest extends BaseRequest {
    private String id;

    private String address;

    private String name;

    private String url;
}
