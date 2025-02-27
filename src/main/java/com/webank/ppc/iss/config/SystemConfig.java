package com.webank.ppc.iss.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "system")
public class SystemConfig {
    private String groupId = "group0";
    private String appId = "ppcAppId";
}
