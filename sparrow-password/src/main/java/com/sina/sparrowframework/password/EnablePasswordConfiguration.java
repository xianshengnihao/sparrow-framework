package com.sina.sparrowframework.password;

import com.sina.sparrowframework.password.licai.AesArgumentConfig;
import com.sina.sparrowframework.password.licai.HandlerMethodAesArgument;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author wxn
 * @date 2021/6/9 4:56 下午
 */
@Configuration
@ComponentScan(value = {"com.sina.sparrowframework.password"})
@ConditionalOnProperty(prefix = "sparrow.password", name = "enable", havingValue = "true", matchIfMissing = true)
@Import({ AesArgumentConfig.class})
public class EnablePasswordConfiguration {





}
