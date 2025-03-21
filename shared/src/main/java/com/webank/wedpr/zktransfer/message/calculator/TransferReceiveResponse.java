package com.webank.wedpr.zktransfer.message.calculator;

import com.webank.wedpr.zktransfer.message.BaseRequest;
import com.webank.wedpr.zktransfer.message.BaseResponse;
import com.webank.wedpr.zktransfer.message.coordinator.MintCommitmentRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=false)
@Data
public class TransferReceiveResponse extends BaseResponse {
    private MintCommitmentRequest receiveProof;
    private byte[] balanceInitialShare;
}