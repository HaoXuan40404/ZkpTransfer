package com.webank.ppc.iss.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetAgencyInfoResponse extends BaseResponse {
    AgencyInfoMessage data;
}
