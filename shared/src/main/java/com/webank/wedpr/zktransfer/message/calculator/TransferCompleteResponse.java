package com.webank.wedpr.zktransfer.message.calculator;

import com.webank.wedpr.zktransfer.message.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper=false)
@Data
public class TransferCompleteResponse extends BaseRequest {
    private List<byte[]> inputRelationShipProofShare;
    private byte[] outputRelationShipProofShare;
}