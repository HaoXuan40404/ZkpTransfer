package com.webank.wedpr.zktransfer.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "agency")
@Slf4j
public class AgencyConfig {

    public String name;
    public String address;

    @Bean
    public String agencyName() {
        return name;
    }

    @Bean
    public String agencyAddress() {
        return address;
    }
}
