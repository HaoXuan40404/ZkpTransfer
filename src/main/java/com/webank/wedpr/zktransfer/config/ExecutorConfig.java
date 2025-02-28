package com.webank.wedpr.zktransfer.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author caryliao
 */
@Configuration
@EnableAsync
@Slf4j
public class ExecutorConfig {

    @Value("${app.core-pool-size:5}")
    int corePoolSize;

    @Value("${app.max-pool-size:10}")
    int maxPoolSize;

    @Value("${app.queue-capacity:100}")
    int queueCapacity;

    @Value("${app.keep-alive-seconds:60}")
    int keepAliveSeconds = 60;

    @Bean(name = "asyncExecutor")
    public ThreadPoolTaskExecutor asyncExecutor() {
        log.info("jobEventQueueThread corePoolSize: {}, maxPoolSize: {}, queueCapacity: {}, keepAliveSeconds: {}",
                corePoolSize, maxPoolSize, queueCapacity, keepAliveSeconds);
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(corePoolSize);
        threadPoolTaskExecutor.setMaxPoolSize(maxPoolSize);
        threadPoolTaskExecutor.setQueueCapacity(queueCapacity);
        threadPoolTaskExecutor.setKeepAliveSeconds(keepAliveSeconds);
        threadPoolTaskExecutor.setThreadNamePrefix("jobEventQueueThread");
        // 队列满的情况, 不在新线程中执行任务,而是有调用者所在的线程来执行
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }

    @Bean(name = "asyncExecutorShedlock")
    public ThreadPoolTaskExecutor asyncExecutorShedlock() {
        log.info("Shedlock corePoolSize: {}, maxPoolSize: {}, queueCapacity: {}, keepAliveSeconds: {}",
                corePoolSize, maxPoolSize, queueCapacity, keepAliveSeconds);
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(corePoolSize);
        threadPoolTaskExecutor.setMaxPoolSize(maxPoolSize);
        threadPoolTaskExecutor.setQueueCapacity(queueCapacity);
        threadPoolTaskExecutor.setKeepAliveSeconds(keepAliveSeconds);
        threadPoolTaskExecutor.setThreadNamePrefix("Shedlock");
        // 队列满的情况, 不在新线程中执行任务,而是有调用者所在的线程来执行
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }

}
