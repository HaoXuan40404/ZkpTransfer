package com.webank.ppc.iss;

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
public class PpcIssApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(PpcIssApplication.class)
                .properties("spring.config.name:application")
                .run(args);
        System.out.println("Start Ppc Iss Application successfully!");
        System.out.println("Swagger URL(Dev Mode): http://localhost:5840/swagger-ui/index.html");
    }
}
