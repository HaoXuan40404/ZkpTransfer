package com.webank.wedpr.zktransfer.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper=false)
@Data
public class ChainWithdrawRequest {
    private List<byte[]> valueProofsList;
    private List<byte[]> knwoledProofsList;
    private List<byte[]> commitmentsList;
    private List<Integer> amountList;
}