package com.webank.wedpr.zktransfer.message.calculator;

import com.webank.wedpr.zktransfer.message.BaseRequest;
import com.webank.wedpr.zktransfer.message.BaseResponse;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper=false)
@Data
public class TransferCompleteResponse extends BaseResponse {
    private List<byte[]> inputRelationShipProofShare;
    private byte[] outputRelationShipProofShare;
}