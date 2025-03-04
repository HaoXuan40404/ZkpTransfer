package com.webank.wedpr.zktransfer.controller;

import com.webank.wedpr.zktransfer.common.EnumResponseStatus;
import com.webank.wedpr.zktransfer.message.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/wedpr/zktransfer/")
public class ZkpTransferController {

    private void setSuccessMsg(BaseResponse response) {
        response.setErrorCode(EnumResponseStatus.SUCCESS.getErrorCode());
        response.setMessage(EnumResponseStatus.SUCCESS.getMessage());
    }


    @PostMapping("/key")
    public BaseResponse uploadCustomerKey(
            @Validated @RequestBody UploadCustomerKeyRequest request) {
        
        // TODO: 
        BaseResponse baseResponse = new BaseResponse();
        return baseResponse;
    }

    @DeleteMapping("/key")
    public BaseResponse deleteCustomerKey(
            @Validated @RequestBody DeleteCustomerKeyRequest request) {
        
        // TODO: 
        BaseResponse baseResponse = new BaseResponse();
        return baseResponse;
    }

    @GetMapping("/key/{accountAddress}")
    public GetCustomerKeyResponse GetCustomerKey(
        @PathVariable("accountAddress") String accountAddress) {
        
        // TODO: 
        GetCustomerKeyResponse baseResponse = new GetCustomerKeyResponse();
        return baseResponse;
    }

    @PostMapping("/deposit")
    public DepositResponse deposit(
            @Validated @RequestBody DepositRequest request) {
        
        // TODO: 
        DepositResponse response = new DepositResponse();
        return response;
    }

    @PostMapping("/withdraw")
    public WithdrawResponse withdraw(
            @Validated @RequestBody WithdrawRequest request) {
        
        // TODO: 
        WithdrawResponse response = new WithdrawResponse();
        return response;
    }

    @PostMapping("/transfer")
    public TransferResponse transfer(
            @Validated @RequestBody TransferRequest request) {
        
        // TODO: 
        TransferResponse response = new TransferResponse();
        return response;
    }

    @GetMapping("/transactions/{accountAddress}")
    public TransactionsResponse getTransactions(
        @PathVariable("accountAddress") String accountAddress,
        @RequestParam(value = "start_block", required = false) Long startBlock,
        @RequestParam(value = "end_block", required = false) Long endBlock,
        @RequestParam(value = "page_size", required = false) Integer pageSize) {
        
        // TODO: 
        TransactionsResponse response = new TransactionsResponse();
        return response;
    }

    @GetMapping("/balance/{accountAddress}")
    public BalanceResponse getBalance(
        @PathVariable("accountAddress") String accountAddress) {
        
        // TODO: 
        BalanceResponse response = new BalanceResponse();
        return response;
    }

}
