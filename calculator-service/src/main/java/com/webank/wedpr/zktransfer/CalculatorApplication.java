package com.webank.wedpr.zktransfer;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.webank")
@EnableScheduling
@EnableAsync
@EnableRetry
public class CalculatorApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(CalculatorApplication.class)
                .properties("spring.config.name:application")
                .run(args);
        System.out.println("Start Calculator Application successfully!");
    }
}
