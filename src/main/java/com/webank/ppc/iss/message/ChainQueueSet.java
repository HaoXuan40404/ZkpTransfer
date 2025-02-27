package com.webank.ppc.iss.message;

import lombok.Data;

import java.math.BigInteger;

@Data
public class ChainQueueSet {
    private String tableName;
    private String metaKey;
    private String metaValue;
    private BigInteger blockNumber;
}
