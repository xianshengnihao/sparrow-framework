package com.sina.sparrowframework.redis.conf;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.redis.spring.RedisLockProvider;
import net.javacrumbs.shedlock.spring.ScheduledLockConfiguration;
import net.javacrumbs.shedlock.spring.ScheduledLockConfigurationBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;

@Configuration
@ConditionalOnClass(value = {RedisTemplate.class})
public class SchedulerConf implements EnvironmentAware {

    private Environment environment;

    @Bean
    @ConditionalOnBean(value = {RedisTemplate.class})
    public LockProvider lockProvider(RedisTemplate redisTemplate) {
        return new RedisLockProvider(redisTemplate.getConnectionFactory(),environment.getProperty("roc.scheduled.lock.env","default"));
    }

    @Bean
    @ConditionalOnBean(value = {LockProvider.class})
    public ScheduledLockConfiguration taskScheduler(LockProvider lockProvider) {
        return ScheduledLockConfigurationBuilder
                .withLockProvider(lockProvider)
                .withPoolSize(environment.getProperty("job.scheduled.lock.pool.size", int.class, 20))
                .withDefaultLockAtMostFor(Duration.ofMinutes(environment.getProperty("job.scheduled.lock.most.minutes", int.class, 10)))
                .withDefaultLockAtLeastFor(Duration.ofSeconds(environment.getProperty("job.scheduled.lock.most.seconds", int.class, 10)))
                .build();
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
