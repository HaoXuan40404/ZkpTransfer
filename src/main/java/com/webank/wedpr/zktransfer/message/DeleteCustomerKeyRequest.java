package com.webank.wedpr.zktransfer.message;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DeleteCustomerKeyRequest extends BaseRequest {
//    @NotBlank(message = "accountAddress must not be blank")
    private String accountAddress;

    private  LocalDateTime timestamp;
}
