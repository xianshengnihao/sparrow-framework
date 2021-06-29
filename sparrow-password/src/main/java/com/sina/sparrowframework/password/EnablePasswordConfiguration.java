package com.sina.sparrowframework.password;

import com.sina.sparrowframework.password.conf.PasswordScanConf;
import com.sina.sparrowframework.password.licai.AesArgumentConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author wxn
 * @date 2021/6/9 4:56 下午
 */
@Configuration
@Import({ AesArgumentConfig.class, PasswordScanConf.class})
public class EnablePasswordConfiguration {




}
