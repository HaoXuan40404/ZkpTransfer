package com.webank.wedpr.zktransfer.config;

import lombok.Data;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
@ConditionalOnProperty(name = "shedlock.enabled", havingValue = "true")
@Data
public class ShedConfig {

    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    @Value("${app.shedlock.lockAtLeastFor}")
    private String lockAtLeastFor;

    @Value("${app.shedlock.lockAtMostFor}")
    private String lockAtMostFor;

//    @Bean
//    public LockProvider lockProvider(DataSource dataSource) {
//        JdbcTemplateLockProvider jdbcTemplateLockProvider = new JdbcTemplateLockProvider(dataSource);
////         TODO: 如果异常了怎么办
//        return new KeepAliveLockProvider(jdbcTemplateLockProvider, executorService);
//    }

    @Bean
    public LockProvider lockProvider(DataSource dataSource) {
        return new JdbcTemplateLockProvider(dataSource);
    }

    @PreDestroy
    public void destroy() {
        executorService.shutdown();
    }
}
