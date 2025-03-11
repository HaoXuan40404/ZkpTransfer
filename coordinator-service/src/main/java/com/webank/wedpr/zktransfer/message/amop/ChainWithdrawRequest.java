package com.webank.wedpr.zktransfer.message.amop;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=false)
@Data
public class ChainWithdrawRequest {
    private byte[] proof;
    private byte[] commitment;
}