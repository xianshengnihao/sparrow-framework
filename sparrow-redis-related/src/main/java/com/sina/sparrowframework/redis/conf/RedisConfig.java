package com.sina.sparrowframework.redis.conf;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sina.sparrowframework.redis.lock.RedisDistLockProvider;
import com.sina.sparrowframework.tools.utility.JacksonUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * redis template configuation
 */
@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport implements EnvironmentAware {

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    /**
     * <p>Jackson serializer redis template</p>
     * @param redisConnectionFactory
     * @return RedisTemplate<String, Object>
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean(name = "jsonRedisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        objectMapper.setTimeZone(JacksonUtil.getDefaultTimeZone());
        objectMapper.registerModule(JacksonUtil.createJavaTimeModule());
        serializer.setObjectMapper(objectMapper);
        template.setValueSerializer(serializer);
        template.setKeySerializer(new StringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }


    /**
     * 分布式锁
     * @param redisTemplate
     * @return
     */
    @Bean
    @ConditionalOnBean(value = {RedisTemplate.class})
    public RedisDistLockProvider redisDistributedLockProvider(RedisTemplate redisTemplate) {
        String envKey = environment.getProperty("spring.cloud.zookeeper.config.root", String.class, "default");
        return new RedisDistLockProvider(redisTemplate, envKey);
    }


}
