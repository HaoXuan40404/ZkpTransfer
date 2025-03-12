package com.webank.wedpr.zktransfer.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper=false)
@Data
public class ChainTransferInitialRequest extends BaseRequest {
    // 发送的钱
    private List<ChainWithdrawRequest> inputInfos;
    // 找零给自己的钱
    private ChainDepositRequest changeInfos;
    // 给银行的钱
    private String receiverBankName;
    private int receiverAmount;
}