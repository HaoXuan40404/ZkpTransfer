package com.webank.wedpr.zktransfer.message.coordinator;

import com.webank.wedpr.zktransfer.message.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper=false)
@Data
public class BurnCommitmentRequest extends BaseRequest {
    private List<byte[]> valueProofsList;
    private List<byte[]> knwoledProofsList;
    private List<byte[]> commitmentsList;
    private List<Integer> amountList;
}