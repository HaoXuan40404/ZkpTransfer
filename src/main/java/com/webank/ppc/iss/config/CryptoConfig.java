package com.webank.ppc.iss.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "obfuscation")
public class CryptoConfig {
    public String key = "0123456789abcdef";
}
