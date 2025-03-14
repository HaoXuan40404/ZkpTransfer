package com.webank.wedpr.zktransfer.service;

import com.webank.wedpr.crypto.zkp.WedprException;
import com.webank.wedpr.zktransfer.message.BaseResponse;
import com.webank.wedpr.zktransfer.message.calculator.TransferCompleteRequest;
import com.webank.wedpr.zktransfer.message.calculator.TransferCompleteResponse;
import com.webank.wedpr.zktransfer.message.calculator.TransferReceiveRequest;
import com.webank.wedpr.zktransfer.message.calculator.TransferReceiveResponse;
import com.webank.wedpr.zktransfer.message.coordinator.TransferStatusUpdateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Slf4j
@Component
public class CalculatorClient {

    @Autowired
    private RestTemplate restTemplate;

    private static final String TRANSFER_RECEIVE = "/transferReceive";
    private static final String TRANSFER_COMPLETE = "/transferComplete";
    private static final String TRANSFER_STATUS_UPDATE = "/transferStatusUpdate";

    private void checkResponse(BaseResponse response) throws WedprException {
        if (response == null || response.getErrorCode() != 0) {
            log.error("response error:{}", response);
            throw new WedprException("response error");
        }
    }

    public TransferReceiveResponse transferReceive(String url, TransferReceiveRequest request) throws WedprException {
        //检查Response是否成功
        log.info("transferReceive: url: {}", url);
        TransferReceiveResponse response = restTemplate.postForObject(url + TRANSFER_RECEIVE, request, TransferReceiveResponse.class);
        checkResponse(response);
        return response;
    }

    public TransferCompleteResponse transferComplete(String url, TransferCompleteRequest request) throws WedprException
    {
        log.info("transferComplete: url: {}", url);
        TransferCompleteResponse response = restTemplate.postForObject(url + TRANSFER_COMPLETE, request, TransferCompleteResponse.class);
        checkResponse(response);
        return response;
    }

    public BaseResponse transferNotifyStatus(String url, TransferStatusUpdateRequest request) throws WedprException
    {
        log.info("transferNotifyStatus: url: {}", url);
        BaseResponse response = restTemplate.postForObject(url + TRANSFER_STATUS_UPDATE, request, BaseResponse.class);
        checkResponse(response);
        return response;
    }
}