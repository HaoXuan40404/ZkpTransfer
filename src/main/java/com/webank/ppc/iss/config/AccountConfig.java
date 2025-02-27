package com.webank.ppc.iss.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "account")
public class AccountConfig {
    private String hexPrivateKey;
    private String keyStoreDir;
    private String accountAddress;
    private String accountPassword;
    private String accountFileFormat;
}
