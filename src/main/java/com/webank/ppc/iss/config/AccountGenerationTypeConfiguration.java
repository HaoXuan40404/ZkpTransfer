package com.webank.ppc.iss.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.GenerationType;

/**
 * @author caryliao
 */
@Configuration
public class AccountGenerationTypeConfiguration {
    @Bean
    @ConditionalOnProperty(name = "spring.datasource.driver-class-name", havingValue = "com.mysql.cj.jdbc.Driver")
    public GenerationType identityGenerationType() {
        return GenerationType.IDENTITY;
    }

    @Bean
    @ConditionalOnProperty(name = "spring.datasource.driver-class-name",
            havingValue = "org.hibernate.dialect.DmDialect")
    public GenerationType sequenceGenerationType() {
        return GenerationType.SEQUENCE;
    }
}
