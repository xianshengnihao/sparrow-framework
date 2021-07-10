package com.sina.sparrowframework.log.conf;

import com.sina.sparrowframework.log.adaptor.SparrowLogInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author wxn
 * @date 2021/6/29 4:30 下午
 */
@Configuration
@ComponentScan(value = {"com.sina.sparrowframework.log"})
public class LogScanConf {

    /**
     * 初始化拦截器
     * @return
     */
    @Bean
    public SparrowLogInterceptor sparrowLogInterceptor (){
        return new SparrowLogInterceptor();
    }
}
