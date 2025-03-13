package com.webank.wedpr.zktransfer.controller;

import com.webank.wedpr.crypto.zkp.WedprException;
import com.webank.wedpr.zktransfer.common.CommitmentStatus;
import com.webank.wedpr.zktransfer.common.EnumResponseStatus;
import com.webank.wedpr.zktransfer.common.Utils;
import com.webank.wedpr.zktransfer.config.AgencyConfig;
import com.webank.wedpr.zktransfer.message.*;

import com.webank.wedpr.zktransfer.message.calculator.*;
import com.webank.wedpr.zktransfer.message.coordinator.MintCommitmentRequest;
import com.webank.wedpr.zktransfer.message.coordinator.BurnCommitmentRequest;
import com.webank.wedpr.zktransfer.message.coordinator.TransferCommitmentRequest;
import com.webank.wedpr.zktransfer.message.coordinator.TransferStatusUpdateResponse;
import com.webank.wedpr.zktransfer.service.CoordinatorClient;
import com.webank.wedpr.zktransfer.service.TransferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/wedpr/pls/")
public class CalculatorController {

    //记录交易和对应commitment
    private final Map<String, TransferRecord> transferRecordMap = new HashMap<>();

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
            MintCommitmentRequest mintCommitmentRequest = transferService.deposit(request);
            // 设置uuid和本机构信息
            setAgencyInfoWithUuid(mintCommitmentRequest);
            // 调用协调服务的deposit接口
            BaseResponse response = coordinatorClient.deposit(mintCommitmentRequest);
            //更新DB状态
            transferService.updateCommitmentStatus(mintCommitmentRequest.getCommitment(), CommitmentStatus.Unspent.getValue());
            return response;
        } catch (Exception e) {
            log.error("deposit failed,", e);
            throw new WedprException(e);
        }
    }

    @PostMapping("/withdraw")
    public BaseResponse withdraw(
            @Validated @RequestBody WithdrawRequest request) throws Exception {
        try {
            BurnCommitmentRequest burnCommitmentRequest = transferService.withdraw(request);
            setAgencyInfoWithUuid(burnCommitmentRequest);
            // 调用协调服务的接口
            BaseResponse response = coordinatorClient.withdraw(burnCommitmentRequest);
            //更新DB状态
            for (int i = 0; i < burnCommitmentRequest.getCommitmentsList().size(); i++) {
                transferService.updateCommitmentStatus(burnCommitmentRequest.getCommitmentsList().get(i), CommitmentStatus.Spent.getValue());
            }
            return response;
        } catch (Exception e) {
            throw new WedprException(e);
        }
    }

    @PostMapping("/transfer")
    public BaseResponse transfer(
            @Validated @RequestBody TransferRequest request) throws WedprException {
        try {
            TransferRecord transferRecord = transferService.transferInitiate(request);
            TransferCommitmentRequest chainTransferInitialRequest  = transferRecord.getSenderRequest();
            setAgencyInfoWithUuid(chainTransferInitialRequest);

            transferRecordMap.put(chainTransferInitialRequest.getBizSeq(), transferRecord);
            // 调用协调服务的接口
//            BaseResponse response = coordinatorClient.transfer(chainTransferInitialRequest);
            BaseResponse response = new BaseResponse();
            //更新DB状态
            for (int i = 0; i <chainTransferInitialRequest.getInputInfos().getCommitmentsList().size(); i++) {
                transferService.updateCommitmentStatus(chainTransferInitialRequest.getInputInfos().getCommitmentsList().get(i), CommitmentStatus.Spent.getValue());
            }
            transferService.updateCommitmentStatus(chainTransferInitialRequest.getChangeInfos().getCommitment(), CommitmentStatus.Unspent.getValue());

            //TODO:删除mapping
            return response;
        } catch (Exception e) {
            throw new WedprException(e);
        }

    }

    @PostMapping("/transferReceive")
    public TransferReceiveResponse transferReceive(
            @Validated @RequestBody TransferReceiveRequest request) throws WedprException {
        try {
            TransferRecord transferRecord = transferService.transferNotify(request);
            TransferReceiveResponse transferReceiveResponse = transferRecord.getReceiverResponse();
            setAgencyInfoWithUuid(transferReceiveResponse);

            transferRecordMap.put(transferReceiveResponse.getBizSeq(), transferRecord);

            return transferReceiveResponse;
        } catch (Exception e) {
            throw new WedprException(e);
        }
    }

    @PostMapping("/transferComplete")
    public TransferCompleteResponse transferComplete(
            @Validated @RequestBody TransferCompleteRequest request) throws WedprException {
        try {
            TransferCompleteResponse response = new TransferCompleteResponse();

            TransferRecord record = transferRecordMap.get(request.getBizSeq());

            // 检查记录是否存在以及角色是否有效
            if (record == null) {
                log.info("未找到对应的 TransferRecord 记录，bizSeq: {}", request.getBizSeq());
            } else if ("from".equals(record.getRole())) {
                List<byte[]> senderPart = transferService.transferSenderComplete(request, transferRecordMap.get(request.getBizSeq()).getSenderBlinding(), transferRecordMap.get(request.getBizSeq()).getSenderRequest().getInputInfos().getAmountList());
                byte[] receiverPart = transferService.transferReceiverComplete(request, transferRecordMap.get(request.getBizSeq()).getReceiverBlinding(), transferRecordMap.get(request.getBizSeq()).getSenderRequest().getChangeInfos().getAmount());

                response.setInputRelationShipProofShare(senderPart);
                response.setOutputRelationShipProofShare(receiverPart);
            } else if ("to".equals(record.getRole())) {
                byte[] receiverPart = transferService.transferReceiverComplete(request, transferRecordMap.get(request.getBizSeq()).getReceiverBlinding(), transferRecordMap.get(request.getBizSeq()).getReceiverResponse().getReceiveProof().getAmount());
                response.setOutputRelationShipProofShare(receiverPart);
            } else {
                log.error("未知的 role 类型: {}，bizSeq: {}", record.getRole(), request.getBizSeq());
            }

            response.setBizSeq(request.getBizSeq());
            return response;
        } catch (Exception e) {
            throw new WedprException(e);
        }
    }

    @PostMapping("/transferStatusUpdate")
    public void transferStatusUpdate(
            @Validated @RequestBody TransferStatusUpdateResponse request) throws WedprException {
        try {
            //TODO:删除mapping
            transferService.updateCommitmentStatus(request.getCommitment(), CommitmentStatus.Unspent.getValue());

        } catch (Exception e) {
            throw new WedprException(e);
        }
    }

}
