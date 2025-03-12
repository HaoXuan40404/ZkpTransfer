package com.webank.wedpr.zktransfer.controller;

import com.webank.wedpr.crypto.zkp.WedprException;
import com.webank.wedpr.zktransfer.common.CommitmentStatus;
import com.webank.wedpr.zktransfer.common.EnumResponseStatus;
import com.webank.wedpr.zktransfer.common.Utils;
import com.webank.wedpr.zktransfer.config.AgencyConfig;
import com.webank.wedpr.zktransfer.message.*;

import com.webank.wedpr.zktransfer.message.calculator.DepositRequest;
import com.webank.wedpr.zktransfer.message.calculator.WithdrawRequest;
import com.webank.wedpr.zktransfer.message.coordinator.ChainDepositRequest;
import com.webank.wedpr.zktransfer.message.coordinator.ChainDepositResponse;
import com.webank.wedpr.zktransfer.message.coordinator.ChainWithdrawRequest;
import com.webank.wedpr.zktransfer.message.coordinator.ChainWithdrawResponse;
import com.webank.wedpr.zktransfer.service.CoordinatorClient;
import com.webank.wedpr.zktransfer.service.TransferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/wedpr/pls/")
public class CalculatorController {

    @Autowired
    TransferService transferService;

    @Autowired
    CoordinatorClient coordinatorClient;

    @Autowired
    AgencyConfig agencyConfig;

    private void setSuccessMsg(BaseResponse response) {
        response.setErrorCode(EnumResponseStatus.SUCCESS.getErrorCode());
        response.setMessage(EnumResponseStatus.SUCCESS.getMessage());
    }

    private void setAgencyInfoWithUuid(BaseRequest request)
    {
        request.setBizSeq(Utils.getUuid());
        request.setAgencyId(agencyConfig.getAddress());
        request.setAgencyName(agencyConfig.getName());
    }

    @PostMapping("/deposit")
    public BaseResponse deposit(
            @Validated @RequestBody DepositRequest request) throws WedprException {
        
        // TODO:
        try {
            ChainDepositRequest chainDepositRequest = transferService.deposit(request);
            // 设置uuid和本机构信息
            setAgencyInfoWithUuid(chainDepositRequest);
            // 调用协调服务的deposit接口
            ChainDepositResponse response = coordinatorClient.deposit(chainDepositRequest);
            //更新DB状态
            transferService.updateCommitmentStatus(chainDepositRequest.getCommitment(), CommitmentStatus.Unspent.getValue());
            return response;
        } catch (Exception e) {
            log.error("deposit failed,", e);
            throw new WedprException(e);
        }
    }

    @PostMapping("/withdraw")
    public ChainWithdrawResponse withdraw(
            @Validated @RequestBody WithdrawRequest request) throws Exception {
        try {
            ChainWithdrawRequest chainWithdrawRequest = transferService.withdraw(request);
            setAgencyInfoWithUuid(chainWithdrawRequest);
            // 调用协调服务的接口
            ChainWithdrawResponse response = coordinatorClient.withdraw(chainWithdrawRequest);
            //更新DB状态
            for (int i = 0; i <chainWithdrawRequest.getCommitmentsList().size(); i++) {
                transferService.updateCommitmentStatus(chainWithdrawRequest.getCommitmentsList().get(i), CommitmentStatus.Spent.getValue());
            }
            return response;
        } catch (Exception e) {
            throw new WedprException(e);
        }
    }

//    @PostMapping("/transfer")
//    public TransferResponse transfer(
//            @Validated @RequestBody TransferRequest request) {
//
//        // TODO:
//        TransferResponse response = new TransferResponse();
//        return response;
//    }
//
//    @GetMapping("/transactions/{accountAddress}")
//    public TransactionsResponse getTransactions(
//        @PathVariable("accountAddress") String accountAddress,
//        @RequestParam(value = "start_block", required = false) Long startBlock,
//        @RequestParam(value = "end_block", required = false) Long endBlock,
//        @RequestParam(value = "page_size", required = false) Integer pageSize) {
//
//        // TODO:
//        TransactionsResponse response = new TransactionsResponse();
//        return response;
//    }
//
//    @GetMapping("/balance/{accountAddress}")
//    public BalanceResponse getBalance(
//        @PathVariable("accountAddress") String accountAddress) {
//
//        // TODO:
//        BalanceResponse response = new BalanceResponse();
//        return response;
//    }

}
