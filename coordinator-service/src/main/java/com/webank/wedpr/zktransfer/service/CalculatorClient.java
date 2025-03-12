package com.webank.wedpr.zktransfer.service;

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

    private static final String DEPOSIT_API = "/transfer";
//    private static final String WITHDRAW_API = "/withdraw";

//    public ChainDepositResponse deposit(ChainDepositRequest request) {
//        return restTemplate.postForObject(coordinatorConfig.getCoordinatorUrl() + DEPOSIT_API, request, ChainDepositResponse.class);
//    }
}