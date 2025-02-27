package com.webank.ppc.iss.config;

import com.alibaba.druid.pool.DruidDataSource;
import java.util.Map;

import com.webank.ppc.iss.utils.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceConfig {
    private static final Logger logger = LoggerFactory.getLogger(DataSourceConfig.class);

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    @Value("${spring.datasource.defaultAutoCommit}")
    private boolean defaultAutoCommit;

    @Value("${spring.datasource.initialSize}")
    private int initialSize;

    @Value("${spring.datasource.maxActive}")
    private int maxActive;

    @Value("${spring.datasource.maxIdle}")
    private int maxIdle;

    @Value("${spring.datasource.minIdle}")
    private int minIdle;

    @Value("${spring.datasource.queryTimeout}")
    private int queryTimeout = 0;

    @Value("${spring.datasource.keepAlive}")
    private boolean keepAlive;

    @Value("${spring.datasource.testOnBorrow}")
    private boolean testOnBorrow;

    @Value("${spring.datasource.testOnReturn}")
    private boolean testOnReturn;

    @Value("${spring.datasource.testWhileIdle}")
    private boolean testWhileIdle;

    @Value("${spring.datasource.maxWait}")
    private long maxWait;

    @Value("${spring.datasource.timeBetweenEvictionRunsMillis}")
    private long timeBetweenEvictionRunsMillis;

    @Value("${spring.datasource.minEvictableIdleTimeMillis}")
    private long minEvictableIdleTimeMillis;

    @Value("${spring.datasource.validationQuery}")
    private String validationQuery;

    @Value("${spring.datasource.validationQueryTimeout}")
    private int validationQueryTimeout;

    @Value("${spring.datasource.removeAbandoned}")
    private boolean removeAbandoned;

    @Value("${spring.datasource.logAbandoned}")
    private boolean logAbandoned;

    @Value("${spring.datasource.removeAbandonedTimeout}")
    private int removeAbandonedTimeout;

    @Value("${spring.datasource.poolPreparedStatements}")
    private boolean poolPreparedStatements;

    @Value("${spring.datasource.connectionProperties}")
    private String connectionProperties;

    @Value("${spring.datasource.filters}")
    private String filters;

    @Value("${spring.datasource.appKey}")
    private String APP_KEY;

    // 子系统私钥，安全说hard core在代码中，用作AOMP两次加密
    public static final String AOMP_PUBLICK_KEY =
            "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEA0btzsPQAK6Ixddk6mNZ+ErDRtIe/9k8rhycp9YjhH6yWACmVIACk5EbxPKPRmVaVwMYjIw5e19t4TRAg9L0+YCAev7YSHReLUIV/7cYd35hXL7bpy8uT4zY2FYfNjjR1BcQlQUzTict+pq8OeeD80u9X0bhrEis8OdLJKmytD7Ehn0BV1iIA9LCsyqFU4bL+xB98blagx7ruuZXMbFelaal4F19U1A4xTniH/IFA3rb9/whXQc7Gy9yM9zs/DtohQGCM+snj2lscIRhauUzw6GQB+3WFLcByjlvvUKUHRxOLFtTHTpjzmG+NMQpXMbkoM7Y0s71CIY9/mHjCtdaJ9+a1rgx7X2Rs6YEj5zJ3vxGwxhkfDDWxXSLTcNt/yQqLFDeQTIgM8fx/pdWLk1sI4uRnzTauNXVjkyXHIpYFyXUggGVVmjZosm+jtg5EsbmHIc5QnPX7IC3ZWsw68yAo4t5Hp2J3QKxa3OgpcRqjXsq0Dj/FPLWh1huWKS5UD3NtRO8zB8UJP+OIGG1yQMTkHLD0ihCsLXvJgVZyspMLSk/rDbnxUBPbARR1mN9vcfh8iEePnZKwiMIAshsTFKxEvFOfHH2J0zspbeilCK7re9FSYatnrQFkOodWXd9gcUb36yQ0u5MNNiQa7iwlgaG9X5oPHnk57d+biH8esTK4bP0CAwEAAQ==";


    @Bean
    public DruidDataSource dataSource() throws Exception {

        logger.info("dataSource dbUrl:{}", url);
        logger.info("dataSource dbUsername:{}", username);
        logger.info("dataSource driverClassName:{}", driverClassName);
        logger.info("dataSource queryTimeout:{}", queryTimeout);

        // https://github.com/alibaba/druid/wiki/DruidDataSource%E9%85%8D%E7%BD%AE

        DruidDataSource datasource = new DruidDataSource();
        datasource.setUrl(url);
        datasource.setUsername(username);
        // 大于32才启用行内加解密
//        String decryptedPassword =
//                StringUtils.length(password) > 32
//                        ? SecurityUtils.decrypt(password, APP_KEY, AOMP_PUBLICK_KEY)
//                        : password;
        String decryptedPassword;
        try {
            decryptedPassword =
                    StringUtils.length(password) > 32
                            ? SecurityUtils.decrypt(password, APP_KEY)
                            : password;
        }
        catch (Exception e) {
            logger.info("decryptedPassword error : {}", e.getMessage());
            throw new Exception(e);
        }

//        logger.info("dataSource decryptedPassword:{}", decryptedPassword);
        datasource.setPassword(decryptedPassword);
        datasource.setDriverClassName(driverClassName);
        datasource.setDefaultAutoCommit(defaultAutoCommit);
        // configuration
        datasource.setInitialSize(initialSize);
        datasource.setMaxActive(maxActive);
        datasource.setMinIdle(maxIdle);
        datasource.setMinIdle(minIdle);
        datasource.setTestOnBorrow(testOnBorrow);
        datasource.setTestOnReturn(testOnReturn);
        datasource.setTestWhileIdle(testWhileIdle);
        datasource.setValidationQuery(validationQuery);
        datasource.setValidationQueryTimeout(validationQueryTimeout);
        datasource.setQueryTimeout(queryTimeout);
        //
        datasource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        datasource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        // datasource.setMaxEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        datasource.setKeepAlive(keepAlive);

        datasource.setRemoveAbandoned(removeAbandoned);
        datasource.setLogAbandoned(logAbandoned);
        datasource.setRemoveAbandonedTimeout(removeAbandonedTimeout);

        datasource.setMaxWait(maxWait);
        datasource.setPoolPreparedStatements(poolPreparedStatements);
        datasource.setConnectionProperties(connectionProperties);
        datasource.setFilters(filters);

        datasource.init();
        Map<String, Object> statData = datasource.getStatData();
        logger.info("创建数据源成功: {}", statData);

        return datasource;
    }
}
