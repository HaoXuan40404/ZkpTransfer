package com.webank.wedpr.zktransfer.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.springframework.beans.factory.annotation.Autowired;
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
    public String privateKey;

    @Autowired
    private ServerConfig serverConfig;

    @Bean
    public byte[] agencyPrivateKey() {
        System.out.println("servicePrivateKey:" + privateKey);
        return Hex.decode(privateKey);
    }

    @Bean
    public String agencyUrl() {
        return "http://" + serverConfig.getServerAddress() + ":" + serverConfig.getServerPort();
    }

    @Bean
    public String agencyName() {
        return name;
    }

    @Bean
    public String agencyAddress() {
        return address;
    }
}
