package com.webank.wedpr.zktransfer.message.coordinator;

import com.webank.wedpr.zktransfer.message.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=false)
@Data
public class ChainTransferInitialRequest extends BaseRequest {
    // 发送的钱
    private ChainWithdrawRequest inputInfos;
    private byte[] inputBalanceInitialShare;
    // 找零给自己的钱
    private ChainDepositRequest changeInfos;
    private byte[] changeBalanceInitialShare;
    // 给银行的钱
    private String receiverBankName;
    private int receiverAmount;
}