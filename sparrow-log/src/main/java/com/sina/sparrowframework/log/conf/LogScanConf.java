package com.sina.sparrowframework.log.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author wxn
 * @date 2021/6/29 4:30 下午
 */
@Configuration
@ComponentScan(value = {"com.sina.sparrowframework.log"})
public class LogScanConf implements WebMvcConfigurer {

}
