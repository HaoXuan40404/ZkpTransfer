package com.webank.ppc.iss.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;

@Slf4j
public class PpcCommonUtils {

    // retry parameters
    public static final int MAX_ATTEMPTS = 3;
    public static final long DELAY = 1000L;
    public static final double MULTIPLIER = 2;

    public static final String PSS_API = "/api/ppc-scheduler/pss/jobs-run";
    public static final String PSS_KILL_API = "/api/ppc-scheduler/pss/jobs-kill";
    public static final long DEFAULT_EMPTY_VALUE = -1L;
    public static final String DEFAULT_EMPTY_STR = "";
    public static final int DEFAULT_PAGE_OFFSET = 0;
    public static final int DEFAULT_PAGE_SIZE = 3;

    public static String sendRequest(String url, Object request, int timeoutInSeconds) {
        RestTemplate restTemplate = new RestTemplate();

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(timeoutInSeconds * 1000);
        requestFactory.setReadTimeout(timeoutInSeconds * 1000);
        restTemplate.setRequestFactory(requestFactory);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity(request, headers);
        ResponseEntity<String> responseEntity =
                restTemplate.postForEntity(url, entity, String.class);
        String response = responseEntity.getBody();
        return response;
    }

    public static boolean hashValue(long value) {
        return value != DEFAULT_EMPTY_VALUE;
    }

    public static BigInteger getTimeStamp() {
        return BigInteger.valueOf(System.currentTimeMillis());
    }
}
