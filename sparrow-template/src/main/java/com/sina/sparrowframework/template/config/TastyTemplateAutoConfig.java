package com.sina.sparrowframework.template.config;

import com.sina.sparrowframework.template.ThymeleafTemplateEngineImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.ITemplateEngine;

/**
 * 自动配置模板转换器
 * created  on 2019-05-06.
 */
@ConditionalOnClass({ITemplateEngine.class})
@ConditionalOnProperty(prefix = "tasty.template",name = {"enable"},havingValue = "true",matchIfMissing = true)
@Configuration
public class TastyTemplateAutoConfig {

    @Bean
    public ThymeleafTemplateEngineImpl tastyThymeleafTemplateEngine(){
        return new ThymeleafTemplateEngineImpl();
    }

}
