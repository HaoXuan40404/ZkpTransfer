package com.webank.ppc.iss.message;

import lombok.Data;

import java.math.BigInteger;

@Data
public class ChainJobEventQueueSet {
    private String jobId;
    private String jobEvent;
    private BigInteger blockNumber;
}
