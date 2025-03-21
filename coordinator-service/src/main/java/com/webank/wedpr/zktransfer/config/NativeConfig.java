package com.webank.wedpr.zktransfer.config;

import com.webank.wedpr.crypto.zkp.NativeInterface;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class NativeConfig {

    @Bean
    public NativeInterface nativeInterface() throws Exception {
        return new NativeInterface();
    }
}
