package com.sina.sparrowframework.log;

import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategyDefault;
import com.netflix.hystrix.strategy.eventnotifier.HystrixEventNotifier;
import com.netflix.hystrix.strategy.executionhook.HystrixCommandExecutionHook;
import com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisher;
import com.netflix.hystrix.strategy.properties.HystrixPropertiesStrategy;
import com.sina.sparrowframework.log.adaptor.LogHystrixConcurrencyStrategy;
import com.sina.sparrowframework.log.interceptor.FeignLogRequestInterceptor;
import feign.RequestInterceptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author wxn
 * @date 2021/8/18 11:41 上午
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({RequestInterceptor.class,HystrixConcurrencyStrategy.class})
public class FeignLogConfiguration {
    private static final Log LOGGER = LogFactory
            .getLog(FeignLogConfiguration.class);
    /**
     * 初始化feign日志拦截器
     * @return feignLogRequestInterceptor
     */
    @Bean("feignLogRequestInterceptor")
    public FeignLogRequestInterceptor feignLogRequestInterceptor(){
        return new FeignLogRequestInterceptor();
    }

    private HystrixConcurrencyStrategy existingConcurrencyStrategy;

    @PostConstruct
    public void init(){
        //初始化 HystrixConcurrencyStrategy 策略
        HystrixEventNotifier eventNotifier = HystrixPlugins.getInstance()
                .getEventNotifier();
        HystrixMetricsPublisher metricsPublisher = HystrixPlugins.getInstance()
                .getMetricsPublisher();
        HystrixPropertiesStrategy propertiesStrategy = HystrixPlugins.getInstance()
                .getPropertiesStrategy();
        HystrixCommandExecutionHook commandExecutionHook = HystrixPlugins.getInstance()
                .getCommandExecutionHook();
        HystrixConcurrencyStrategy concurrencyStrategy = detectRegisteredConcurrencyStrategy();

        HystrixPlugins.reset();

        // Registers existing plugins excepts the Concurrent Strategy plugin.
        HystrixPlugins.getInstance().registerConcurrencyStrategy(
                new LogHystrixConcurrencyStrategy(concurrencyStrategy));
        HystrixPlugins.getInstance().registerEventNotifier(eventNotifier);
        HystrixPlugins.getInstance().registerMetricsPublisher(metricsPublisher);
        HystrixPlugins.getInstance().registerPropertiesStrategy(propertiesStrategy);
        HystrixPlugins.getInstance().registerCommandExecutionHook(commandExecutionHook);

    }
    private HystrixConcurrencyStrategy detectRegisteredConcurrencyStrategy() {
        HystrixConcurrencyStrategy registeredStrategy = HystrixPlugins.getInstance()
                .getConcurrencyStrategy();
        if (existingConcurrencyStrategy == null) {
            return registeredStrategy;
        }
        // Hystrix registered a default Strategy.
        if (registeredStrategy instanceof HystrixConcurrencyStrategyDefault) {
            return existingConcurrencyStrategy;
        }
        // If registeredStrategy not the default and not some use bean of
        // existingConcurrencyStrategy.
        if (!existingConcurrencyStrategy.equals(registeredStrategy)) {
            LOGGER.warn(
                    "Multiple HystrixConcurrencyStrategy detected. Bean of HystrixConcurrencyStrategy was used.");
        }
        return existingConcurrencyStrategy;
    }
    @Autowired(required = false)
    public void setExistingConcurrencyStrategy(HystrixConcurrencyStrategy existingConcurrencyStrategy) {
        this.existingConcurrencyStrategy = existingConcurrencyStrategy;
    }
}
