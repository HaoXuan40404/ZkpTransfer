package com.webank.wedpr.zktransfer;

import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.webank")
@EnableScheduling
@EnableAsync
@EnableSchedulerLock(defaultLockAtMostFor = "PT30S")
@EnableRetry
public class ZktransferApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ZktransferApplication.class)
                .properties("spring.config.name:application")
                .run(args);
        System.out.println("Start Zk transfer Application successfully!");
        System.out.println("Swagger URL(Dev Mode): http://localhost:5840/swagger-ui/index.html");
    }
}
