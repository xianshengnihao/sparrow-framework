package com.sina.sparrowframework.job;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "xxl.job", name = "admin-addresses")
@EnableConfigurationProperties(XxlJobProperties.class)
@SuppressWarnings("Duplicates")
public class XxlJobAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public XxlJobSpringExecutor xxlJobExecutor(XxlJobProperties properties) {
        XxlJobSpringExecutor executor = new XxlJobSpringExecutor();

        PropertyMapper map = PropertyMapper.get();

        map.from(properties::getAdminAddresses).whenNonNull().to(executor::setAdminAddresses);
        map.from(properties::getExecutorAppname).whenNonNull().to(executor::setAppname);
        map.from(properties::getExecutorAddress).whenNonNull().to(executor::setAddress);
        map.from(properties::getExecutorIp).whenNonNull().to(executor::setIp);

        map.from(properties::getExecutorPort).whenNonNull().to(executor::setPort);
        map.from(properties::getAccessToken).whenNonNull().to(executor::setAccessToken);
        map.from(properties::getExecutorLogPath).whenNonNull().to(executor::setLogPath);
        map.from(properties::getExecutorLogRetentionDays).whenNonNull()
                .to(executor::setLogRetentionDays);

        return executor;
    }
}
