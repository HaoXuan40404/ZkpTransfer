package com.webank.wedpr.zktransfer.message;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UploadCustomerKeyRequest extends BaseRequest {
    private String accountAddress;

    private String accountLey;

    private  LocalDateTime timestamp;
}
