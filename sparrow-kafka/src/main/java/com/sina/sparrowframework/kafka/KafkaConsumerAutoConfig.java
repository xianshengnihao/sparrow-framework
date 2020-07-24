package com.sina.sparrowframework.kafka;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;

import java.time.Duration;

/**
 * {@link org.apache.kafka.clients.consumer.KafkaConsumer} 的配置.
 * <p>
 * 若要使用 KafkaConsumer 服务, 需要显示的设置 <code>sparrow.mq.kafka.consumer.enable=true</code>
 */
@Configuration
@ConditionalOnProperty(prefix = "sparrow.mq.kafka.consumer", name = "enable", havingValue = "true")
@EnableConfigurationProperties({KafkaProperties.class})
@AutoConfigureAfter(KafkaProducerAutoConfig.class)
public class KafkaConsumerAutoConfig {

    public static final String ACK_CONTAINER = "ackKafkaListenerContainerFactory";

    @Bean
    public ConsumerFactory<String, String> consumerFactory(KafkaProperties kafkaProperties) {
        return new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties());
    }

    /**
     * 自动提交的 ContainerFactory, 默认的方式.
     */
    @Bean
    @Primary
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>>
    kafkaListenerContainerFactory(KafkaProperties kafkaProperties,
                                  KafkaTemplate<String, String> kafkaTemplate) {
        return buildKafkaListenerContainerFactory(kafkaProperties, kafkaTemplate, Boolean.FALSE);
    }

    /**
     * 手动提交的 ContainerFactory.
     * <p>
     * 使用时需要显示的指定 {@link KafkaListener#containerFactory()} 为 {@link #ACK_CONTAINER}
     */
    @Bean(name = ACK_CONTAINER)
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>>
    ackKafkaListenerContainerFactory(KafkaProperties kafkaProperties,
                                     KafkaTemplate<String, String> kafkaTemplate) {
        return buildKafkaListenerContainerFactory(kafkaProperties, kafkaTemplate, Boolean.TRUE);
    }

    private ConcurrentKafkaListenerContainerFactory<String, String>
    buildKafkaListenerContainerFactory(KafkaProperties kafkaProperties,
                                       KafkaTemplate<String, String> kafkaTemplate, boolean ack) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        if (ack) {
            kafkaProperties.getConsumer().setEnableAutoCommit(Boolean.FALSE);
            kafkaProperties.getListener().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        }

        factory.setConsumerFactory(consumerFactory(kafkaProperties));

        KafkaProperties.Listener listener = kafkaProperties.getListener();

        factory.setConcurrency(listener.getConcurrency());
        // 配置监听器
        PropertyMapper map = PropertyMapper.get();

        KafkaProperties.Listener properties = kafkaProperties.getListener();
        map.from(properties::getConcurrency).whenNonNull().to(factory::setConcurrency);

        map.from(() -> kafkaTemplate).whenNonNull().to(factory::setReplyTemplate);
        map.from(properties::getType).whenEqualTo(KafkaProperties.Listener.Type.BATCH)
                .toCall(() -> factory.setBatchListener(true));
        // 配置容器
        configureContainer(factory.getContainerProperties(), kafkaProperties);
        return factory;
    }


    private void configureContainer(ContainerProperties container, KafkaProperties kafkaProperties) {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();

        KafkaProperties.Listener properties = kafkaProperties.getListener();

        map.from(properties::getAckMode).to(container::setAckMode);
        map.from(properties::getClientId).to(container::setClientId);
        map.from(properties::getAckCount).to(container::setAckCount);
        map.from(properties::getAckTime).as(Duration::toMillis).to(container::setAckTime);

        map.from(properties::getPollTimeout).as(Duration::toMillis).to(container::setPollTimeout);
        map.from(properties::getNoPollThreshold).to(container::setNoPollThreshold);
        map.from(properties::getIdleEventInterval).as(Duration::toMillis).to(container::setIdleEventInterval);
        map.from(properties::getMonitorInterval).as(Duration::getSeconds).as(Number::intValue).to(container::setMonitorInterval);

        map.from(properties::getLogContainerConfig).to(container::setLogContainerConfig);
    }
}
