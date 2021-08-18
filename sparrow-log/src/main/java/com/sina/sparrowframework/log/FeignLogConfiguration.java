package com.sina.sparrowframework.log;

import com.sina.sparrowframework.log.interceptor.FeignLogRequestInterceptor;
import feign.RequestInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wxn
 * @date 2021/8/18 11:41 上午
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(RequestInterceptor.class)
public class FeignLogConfiguration {

    /**
     * 初始化feign日志拦截器
     * @return feignLogRequestInterceptor
     */
    @Bean("feignLogRequestInterceptor")
    public FeignLogRequestInterceptor feignLogRequestInterceptor(){
        return new FeignLogRequestInterceptor();
    }

}
