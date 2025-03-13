package com.webank.wedpr.zktransfer.message.calculator;

import com.webank.wedpr.zktransfer.message.BaseRequest;
import com.webank.wedpr.zktransfer.message.coordinator.ChainDepositRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=false)
@Data
public class TransferReceiveResponse extends BaseRequest {
    private ChainDepositRequest receiveProof;
    private byte[] balanceInitialShare;
}