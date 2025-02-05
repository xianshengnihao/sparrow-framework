package com.sina.sparrowframework.rocketmq.autoconfigure;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sina.sparrowframework.rocketmq.config.RocketMQConfigUtils;
import com.sina.sparrowframework.rocketmq.config.RocketMQTransactionAnnotationProcessor;
import com.sina.sparrowframework.rocketmq.config.TransactionHandlerRegistry;
import com.sina.sparrowframework.rocketmq.core.RocketMQTemplate;
import com.sina.sparrowframework.tools.utility.Assert;
import com.sina.sparrowframework.tools.utility.StrToolkit;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.AccessChannel;
import org.apache.rocketmq.client.MQAdmin;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Role;
import org.springframework.core.env.Environment;


import javax.annotation.PostConstruct;

@Configuration
@EnableConfigurationProperties(RocketMQProperties.class)
@ConditionalOnClass({ MQAdmin.class, ObjectMapper.class })
@ConditionalOnProperty(prefix = "rocketmq", value = "name-server", matchIfMissing = true)
@Import({ ListenerContainerConfiguration.class, ExtProducerResetConfiguration.class })
@AutoConfigureAfter(JacksonAutoConfiguration.class)
public class RocketMQAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(RocketMQAutoConfiguration.class);

    @Autowired
    private Environment environment;

    @PostConstruct
    public void checkProperties() {
        String nameServer = environment.getProperty("rocketmq.name-server", String.class);
        log.debug("rocketmq.nameServer = {}", nameServer);
        if (nameServer == null) {
            log.warn("The necessary spring property 'rocketmq.name-server' is not defined, all rockertmq beans creation are skipped!");
        }
    }


    @Bean
    @ConditionalOnMissingBean(DefaultMQProducer.class)
    @ConditionalOnProperty(prefix = "rocketmq", value = {"name-server", "producer.group"})
    public DefaultMQProducer defaultMQProducer(RocketMQProperties rocketMQProperties) {
        RocketMQProperties.Producer producerConfig = rocketMQProperties.getProducer();
        String nameServer = rocketMQProperties.getNameServer();
        String groupName = producerConfig.getGroup();
        Assert.hasText(nameServer, "[rocketmq.name-server] must not be null");
        Assert.hasText(groupName, "[rocketmq.producer.group] must not be null");

        String accessChannel = rocketMQProperties.getAccessChannel();

        DefaultMQProducer producer;
        String ak = rocketMQProperties.getProducer().getAccessKey();
        String sk = rocketMQProperties.getProducer().getSecretKey();
        if (!StrToolkit.isEmpty(ak) && !StrToolkit.isEmpty(sk)) {
            producer = new DefaultMQProducer(groupName, new AclClientRPCHook(new SessionCredentials(ak, sk)),
                rocketMQProperties.getProducer().isEnableMsgTrace(),
                rocketMQProperties.getProducer().getCustomizedTraceTopic());
            producer.setVipChannelEnabled(false);
        } else {
            producer = new DefaultMQProducer(groupName, rocketMQProperties.getProducer().isEnableMsgTrace(),
                rocketMQProperties.getProducer().getCustomizedTraceTopic());
        }

        producer.setNamesrvAddr(nameServer);
        if (!StrToolkit.isEmpty(accessChannel)) {
            producer.setAccessChannel(AccessChannel.valueOf(accessChannel));
        }
        producer.setSendMsgTimeout(producerConfig.getSendMessageTimeout());
        producer.setRetryTimesWhenSendFailed(producerConfig.getRetryTimesWhenSendFailed());
        producer.setRetryTimesWhenSendAsyncFailed(producerConfig.getRetryTimesWhenSendAsyncFailed());
        producer.setMaxMessageSize(producerConfig.getMaxMessageSize());
        producer.setCompressMsgBodyOverHowmuch(producerConfig.getCompressMessageBodyThreshold());
        producer.setRetryAnotherBrokerWhenNotStoreOK(producerConfig.isRetryNextServer());

        return producer;
    }

    @Bean(destroyMethod = "destroy")
    @ConditionalOnBean(DefaultMQProducer.class)
    @ConditionalOnMissingBean(name = RocketMQConfigUtils.ROCKETMQ_TEMPLATE_DEFAULT_GLOBAL_NAME)
    public RocketMQTemplate rocketMQTemplate(DefaultMQProducer mqProducer, ObjectMapper rocketMQMessageObjectMapper) {
        RocketMQTemplate rocketMQTemplate = new RocketMQTemplate();
        rocketMQTemplate.setDefaultMQProducer(mqProducer);
        rocketMQTemplate.setObjectMapper(rocketMQMessageObjectMapper);
        return rocketMQTemplate;
    }

    @Bean
    @ConditionalOnBean(name = RocketMQConfigUtils.ROCKETMQ_TEMPLATE_DEFAULT_GLOBAL_NAME)
    @ConditionalOnMissingBean(TransactionHandlerRegistry.class)
    public TransactionHandlerRegistry transactionHandlerRegistry(@Qualifier(RocketMQConfigUtils.ROCKETMQ_TEMPLATE_DEFAULT_GLOBAL_NAME)
                                                                             RocketMQTemplate template) {
        return new TransactionHandlerRegistry(template);
    }

    @Bean(name = RocketMQConfigUtils.ROCKETMQ_TRANSACTION_ANNOTATION_PROCESSOR_BEAN_NAME)
    @ConditionalOnBean(TransactionHandlerRegistry.class)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public static RocketMQTransactionAnnotationProcessor transactionAnnotationProcessor(
        TransactionHandlerRegistry transactionHandlerRegistry) {
        return new RocketMQTransactionAnnotationProcessor(transactionHandlerRegistry);
    }

}
