package com.webank.wedpr.zktransfer.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ToString
@ConfigurationProperties(prefix = "service")
public class ServiceConfig {

    private String jobScheduler;
    private boolean jobEventEnabled;
    private int jobEventTimeout = 30;
    private int scheduleInitialDelay = 30;
    private int scheduleFixedDelay = 500;
    private int jobMaxConcurrency = 5;
    private String agencyId = "WEBANK";
}
