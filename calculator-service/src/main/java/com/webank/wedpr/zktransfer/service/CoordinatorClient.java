package com.webank.wedpr.zktransfer.service;

import com.webank.wedpr.zktransfer.config.CoordinatorConfig;
import com.webank.wedpr.zktransfer.message.coordinator.ChainDepositRequest;
import com.webank.wedpr.zktransfer.message.coordinator.ChainDepositResponse;
import com.webank.wedpr.zktransfer.message.coordinator.ChainWithdrawRequest;
import com.webank.wedpr.zktransfer.message.coordinator.ChainWithdrawResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Slf4j
@Component
public class CoordinatorClient {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CoordinatorConfig coordinatorConfig;

    private static final String DEPOSIT_API = "/deposit";
    private static final String WITHDRAW_API = "/withdraw";

    public ChainDepositResponse deposit(ChainDepositRequest request) {
        return restTemplate.postForObject(coordinatorConfig.getCoordinatorUrl() + DEPOSIT_API, request, ChainDepositResponse.class);
    }

    public ChainWithdrawResponse withdraw(ChainWithdrawRequest request) {
        return restTemplate.postForObject(coordinatorConfig.getCoordinatorUrl() + WITHDRAW_API, request, ChainWithdrawResponse.class);
    }
}