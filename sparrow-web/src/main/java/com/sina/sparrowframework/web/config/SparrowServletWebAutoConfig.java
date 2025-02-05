package com.sina.sparrowframework.web.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sina.sparrowframework.tools.utility.JsonUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * created  on 2019-04-13.
 */
@ConditionalOnProperty(prefix = "sparrow.web", name = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@Configuration
public class SparrowServletWebAutoConfig {

    @Bean
    @ConditionalOnMissingBean(name = {"webObjectMapper"})
    public ObjectMapper webObjectMapper() {
        return JsonUtils.createNonNullObjectMapper();
    }

}
