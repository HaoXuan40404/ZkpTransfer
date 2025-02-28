package com.webank.wedpr.zktransfer.message;

import lombok.Data;
import java.util.List;

@Data
public class TransactionsResponse extends BaseResponse {
    private long total;
    private List<Transaction> transactions;

    @Data
    public static class Transaction {
        private String txHash;
        private String direction;
        private long amount;
        private long blockNumber;
    }
}