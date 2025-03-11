package com.webank.wedpr.zktransfer.message.coordinator;

import java.util.List;

import com.webank.wedpr.zktransfer.message.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=false)
@Data
public class ChainWithdrawRequest extends BaseRequest {
    private List<byte[]> valueProofsList;
    private List<byte[]> knwoledProofsList;
    private List<byte[]> commitmentsList;
    private List<Integer> amountList;
}