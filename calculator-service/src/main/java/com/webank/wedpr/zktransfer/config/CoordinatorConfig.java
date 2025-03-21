package com.webank.wedpr.zktransfer.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class CoordinatorConfig {

    @Value("${coordinator.url}")
    private String coordinatorUrl;

    public String getCoordinatorUrl() {
        return coordinatorUrl;
    }
}
