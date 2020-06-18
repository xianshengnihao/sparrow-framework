package com.sina.sparrowframework.rocketmq.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;


/**
 * <p>如果项目中未添加{@link ObjectMapper},则需要打开以下两个注解</p>
 */
//@Configuration
//@ConditionalOnMissingBean(ObjectMapper.class)
class JacksonFallbackConfiguration {

    @Bean
    public ObjectMapper rocketMQMessageObjectMapper() {
        return new ObjectMapper();
    }

}
