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

        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();

        map.from(properties::getAdminAddresses).to(executor::setAdminAddresses);
        map.from(properties::getExecutorAppname).to(executor::setAppname);
        map.from(properties::getExecutorAddress).to(executor::setAddress);
        map.from(properties::getExecutorIp).to(executor::setIp);

        map.from(properties::getExecutorPort).to(executor::setPort);
        map.from(properties::getAccessToken).to(executor::setAccessToken);
        map.from(properties::getExecutorLogPath).to(executor::setLogPath);
        map.from(properties::getExecutorLogRetentionDays).to(executor::setLogRetentionDays);

        return executor;
    }
}
