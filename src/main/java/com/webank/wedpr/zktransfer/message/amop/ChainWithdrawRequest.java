package com.webank.wedpr.zktransfer.message.amop;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=false)
@Data
public class ChainWithdrawRequest {
    private List<byte[]> proofsList;
    private byte[] commitment;
    private long amount;
    private String account;
}