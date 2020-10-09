package com.sina.sparrowframework.kafka;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * {@link org.apache.kafka.clients.producer.KafkaProducer} 的配置.
 */
@Configuration
@ConditionalOnProperty(prefix = "sparrow.mq.kafka.producer", name = "enable", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties({KafkaProperties.class})
public class KafkaProducerAutoConfig {

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(KafkaProperties kafkaProperties) {
        return new KafkaTemplate<>(kafkaProducerFactory(kafkaProperties));
    }

    @Bean
    public DefaultKafkaProducerFactory<String, String> kafkaProducerFactory(KafkaProperties kafkaProperties) {
        return new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties());
    }
}
