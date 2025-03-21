package com.webank.wedpr.zktransfer.message.coordinator;

import com.webank.wedpr.zktransfer.message.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper=false)
@Data
public class TransferCommitmentRequest extends BaseRequest {
    // 发送的钱
    private BurnCommitmentRequest inputInfos;
    private List<byte[]> inputBalanceInitialShares;
    // 找零给自己的钱
    private MintCommitmentRequest changeInfos;
    private byte[] changeBalanceInitialShare;
    // 给银行的钱
    private String receiverBankName;
    private int receiverAmount;
}