package com.webank.wedpr.zktransfer.controller;

import com.webank.wedpr.crypto.zkp.WedprException;
import com.webank.wedpr.zktransfer.common.EnumResponseStatus;
import com.webank.wedpr.zktransfer.entity.Account;
import com.webank.wedpr.zktransfer.entity.TransactionHistory;
import com.webank.wedpr.zktransfer.message.*;
import com.webank.wedpr.zktransfer.message.coordinator.*;
import com.webank.wedpr.zktransfer.repository.AccountRepository;

import com.webank.wedpr.zktransfer.service.TransferService;
import lombok.extern.slf4j.Slf4j;

import org.hibernate.query.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/wedpr/crs/")
public class CoordinatorController {

    @Autowired private AccountRepository accountRepository;

    @Autowired private TransferService transferService;

    private void setSuccessMsg(BaseResponse response) {
        response.setErrorCode(EnumResponseStatus.SUCCESS.getErrorCode());
        response.setMessage(EnumResponseStatus.SUCCESS.getMessage());
    }

    private void setErrorMsg(BaseResponse response) {
        response.setErrorCode(EnumResponseStatus.FAILURE.getErrorCode());
        response.setMessage(EnumResponseStatus.FAILURE.getMessage());
    }

    @PostMapping("/register")
    public BaseResponse registerBankInfo(
            @Validated @RequestBody RegisterInfoRequest request) {
        
        String accountId = request.getId();
        String accountAddress = request.getAddress();
        String accountName = request.getName();
        String url = request.getUrl();
        BaseResponse baseResponse = new BaseResponse();

        log.info("accountId: {}, accountAddress: {}, accountName: {}", accountId, accountAddress, accountName);
        try {
            Account account = new Account();
            account.setId(accountId);
            account.setAddress(accountAddress);
            account.setName(accountName);
            account.setUrl(url);
            Timestamp currentTimeMillisme = new Timestamp(System.currentTimeMillis());
            account.setCreateTime(currentTimeMillisme);
            account.setUpdateTime(currentTimeMillisme);
            account.setStatus(1); // Assuming 1 is the status for 'normal'
            accountRepository.save(account);
            setSuccessMsg(baseResponse);
        } catch (Exception e) {
            log.error("Create commitment table failed", e);
            setErrorMsg(baseResponse);

        }
        return baseResponse;
    }

    @PostMapping("/deposit")
    public BaseResponse deposit(@Validated @RequestBody MintCommitmentRequest request)  {
        // 协调方先验证证明
        BaseResponse response = null;
        try {
            response = transferService.deposit(request);
        } catch (WedprException e) {
            log.error("deposit failed", e);
            throw new RuntimeException(e);
        }
        setSuccessMsg(response);
        return response;
    }

    @PostMapping("/withdraw")
    public BaseResponse withdraw(@Validated @RequestBody BurnCommitmentRequest request) {
        // 协调方先验证证明
        BaseResponse response = null;
        try {
            response = transferService.withdraw(request);
        } catch (WedprException e) {
            log.error("withdraw failed", e);
            throw new RuntimeException(e);
        }
        setSuccessMsg(response);
        return response;
    }


    @PostMapping("/transfer")
    public BaseResponse transfer(@Validated @RequestBody TransferCommitmentRequest request) {
        BaseResponse response = null;
        try {
            response = transferService.transfer(request);
        } catch (WedprException e) {
            log.error("transfer failed", e);
            throw new RuntimeException(e);
        }
        setSuccessMsg(response);
        return response;
    }

    // 分页查询历史
    @GetMapping("/history")
    public TransactionHistoryResponse getHistory(@RequestParam int page, @RequestParam int pageSize) {
        TransactionHistoryResponse response = new TransactionHistoryResponse();
        try {
            List<TransactionHistoryData> queryHistories = transferService.queryHistory(page, pageSize);
            response.setTransactionHistoryDataList(queryHistories);
            setSuccessMsg(response);
        } catch (Exception e) {
            log.error("Get history failed", e);
            setErrorMsg(response);
        }
        return response;
    }
}
