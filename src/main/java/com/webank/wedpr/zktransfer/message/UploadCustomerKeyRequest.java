package com.webank.wedpr.zktransfer.message;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UploadCustomerKeyRequest extends BaseRequest {
    @NotBlank(message = "accountAddress must not be blank")
    private String accountAddress;

    @NotBlank(message = "accountLey must not be blank")
    private String accountLey;

    private  LocalDateTime timestamp;
}
