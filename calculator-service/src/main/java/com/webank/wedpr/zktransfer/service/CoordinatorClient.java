package com.webank.wedpr.zktransfer.service;

import com.webank.wedpr.crypto.zkp.WedprException;
import com.webank.wedpr.zktransfer.config.CoordinatorConfig;
import com.webank.wedpr.zktransfer.message.BaseResponse;
import com.webank.wedpr.zktransfer.message.coordinator.MintCommitmentRequest;
import com.webank.wedpr.zktransfer.message.coordinator.BurnCommitmentRequest;
import com.webank.wedpr.zktransfer.message.coordinator.TransferCommitmentRequest;
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
    private static final String TRANSFER_API = "/transfer";

     private void checkResponse(BaseResponse response) throws WedprException {
        if (response == null || response.getErrorCode() != 0) {
            log.error("response error:{}", response);
            throw new WedprException("response error");
        }
    }

    public BaseResponse deposit(MintCommitmentRequest request) throws WedprException {
        BaseResponse response = restTemplate.postForObject(coordinatorConfig.getCoordinatorUrl() + DEPOSIT_API, request, BaseResponse.class);
        checkResponse(response);
        return response;
    }

    public BaseResponse withdraw(BurnCommitmentRequest request) throws WedprException {
        BaseResponse response = restTemplate.postForObject(coordinatorConfig.getCoordinatorUrl() + WITHDRAW_API, request, BaseResponse.class);
        checkResponse(response);
        return response;
    }

    public BaseResponse transfer(TransferCommitmentRequest request) throws WedprException
    {
        BaseResponse response = restTemplate.postForObject(coordinatorConfig.getCoordinatorUrl() + TRANSFER_API, request, BaseResponse.class);
        checkResponse(response);
        return response;
    }
}