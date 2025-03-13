package com.webank.wedpr.zktransfer.service;

import com.webank.wedpr.zktransfer.message.calculator.TransferCompleteRequest;
import com.webank.wedpr.zktransfer.message.calculator.TransferCompleteResponse;
import com.webank.wedpr.zktransfer.message.calculator.TransferReceiveRequest;
import com.webank.wedpr.zktransfer.message.calculator.TransferReceiveResponse;
import com.webank.wedpr.zktransfer.message.coordinator.ChainDepositRequest;
import com.webank.wedpr.zktransfer.message.coordinator.ChainDepositResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Slf4j
@Component
public class CalculatorClient {

    @Autowired
    private RestTemplate restTemplate;

    private static final String TRANSFER_RECEIVE = "/transfer_receive";
    private static final String TRANSFER_COMPLETE = "/transfer_complete";
//    private static final String WITHDRAW_API = "/withdraw";

    public TransferReceiveResponse transferReceive(String url, TransferReceiveRequest request) {
        return restTemplate.postForObject(url + TRANSFER_RECEIVE, request, TransferReceiveResponse.class);
    }

    public TransferCompleteResponse transferComplete(String url, TransferCompleteRequest request)
    {
        return restTemplate.postForObject(url + TRANSFER_COMPLETE, request, TransferCompleteResponse.class);
    }
}