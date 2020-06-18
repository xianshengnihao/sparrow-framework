package com.sina.sparrowframework.rocketmq.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sina.sparrowframework.rocketmq.annotation.ExtRocketMQTemplateConfiguration;
import com.sina.sparrowframework.rocketmq.core.RocketMQTemplate;
import com.sina.sparrowframework.tools.utility.StrToolkit;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.support.BeanDefinitionValidationException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.StandardEnvironment;

import java.util.Map;
import java.util.Objects;

/**
 * <p>生产者创建和启动配置类</p>
 */
@Configuration
public class ExtProducerResetConfiguration implements ApplicationContextAware, SmartInitializingSingleton {
    private final static Logger log = LoggerFactory.getLogger(ExtProducerResetConfiguration.class);

    private ConfigurableApplicationContext applicationContext;

    private final StandardEnvironment environment;

    private final RocketMQProperties rocketMQProperties;

    private final ObjectMapper objectMapper;

    public ExtProducerResetConfiguration(final ObjectMapper rocketMQMessageObjectMapper,
            final StandardEnvironment environment, final RocketMQProperties rocketMQProperties) {
        this.objectMapper = rocketMQMessageObjectMapper;
        this.environment = environment;
        this.rocketMQProperties = rocketMQProperties;
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }

    @Override
    public void afterSingletonsInstantiated() {
        final Map<String, Object> beans = this.applicationContext
                .getBeansWithAnnotation(ExtRocketMQTemplateConfiguration.class);

        if (Objects.nonNull(beans)) {
            beans.forEach(this::registerTemplate);
        }
    }

    private void registerTemplate(final String beanName, final Object bean) {
        final Class<?> clazz = AopProxyUtils.ultimateTargetClass(bean);

        if (!RocketMQTemplate.class.isAssignableFrom(bean.getClass())) {
            throw new IllegalStateException(clazz + " is not instance of " + RocketMQTemplate.class.getName());
        }

        final ExtRocketMQTemplateConfiguration annotation = clazz.getAnnotation(ExtRocketMQTemplateConfiguration.class);
        final GenericApplicationContext genericApplicationContext = (GenericApplicationContext) applicationContext;
        validate(annotation, genericApplicationContext);

        final DefaultMQProducer mqProducer = createProducer(annotation);
        // Set instanceName same as the beanName
        mqProducer.setInstanceName(beanName);
        try {
            mqProducer.start();
        } catch (final MQClientException e) {
            throw new BeanDefinitionValidationException(
                    String.format("Failed to startup MQProducer for RocketMQTemplate {}", beanName), e);
        }
        final RocketMQTemplate rocketMQTemplate = (RocketMQTemplate) bean;
        rocketMQTemplate.setDefaultMQProducer(mqProducer);
        rocketMQTemplate.setObjectMapper(objectMapper);

        log.info("Set real producer to :{} {}", beanName, annotation.value());
    }

    private DefaultMQProducer createProducer(final ExtRocketMQTemplateConfiguration annotation) {
        DefaultMQProducer producer = null;

        RocketMQProperties.Producer producerConfig = rocketMQProperties.getProducer();
        if (producerConfig == null) {
            producerConfig = new RocketMQProperties.Producer();
        }
        final String nameServer = environment.resolvePlaceholders(annotation.nameServer());
        String groupName = environment.resolvePlaceholders(annotation.group());
        groupName = StrToolkit.isEmpty(groupName) ? producerConfig.getGroup() : groupName;

        String ak = environment.resolvePlaceholders(annotation.accessKey());
        ak = StrToolkit.isEmpty(ak) ? producerConfig.getAccessKey() : annotation.accessKey();
        String sk = environment.resolvePlaceholders(annotation.secretKey());
        sk = StrToolkit.isEmpty(sk) ? producerConfig.getSecretKey() : annotation.secretKey();
        String customizedTraceTopic = environment.resolvePlaceholders(annotation.customizedTraceTopic());
        customizedTraceTopic = StrToolkit.isEmpty(customizedTraceTopic) ? producerConfig.getCustomizedTraceTopic()
                : customizedTraceTopic;

        if (!StrToolkit.isEmpty(ak) && !StrToolkit.isEmpty(sk)) {
            producer = new DefaultMQProducer(groupName, new AclClientRPCHook(new SessionCredentials(ak, sk)),
                    annotation.enableMsgTrace(), customizedTraceTopic);
            producer.setVipChannelEnabled(false);
        } else {
            producer = new DefaultMQProducer(groupName, annotation.enableMsgTrace(), customizedTraceTopic);
        }

        producer.setNamesrvAddr(nameServer);
        producer.setSendMsgTimeout(annotation.sendMessageTimeout() == -1 ? producerConfig.getSendMessageTimeout()
                : annotation.sendMessageTimeout());
        producer.setRetryTimesWhenSendFailed(
                annotation.retryTimesWhenSendAsyncFailed() == -1 ? producerConfig.getRetryTimesWhenSendFailed()
                        : annotation.retryTimesWhenSendAsyncFailed());
        producer.setRetryTimesWhenSendAsyncFailed(
                annotation.retryTimesWhenSendAsyncFailed() == -1 ? producerConfig.getRetryTimesWhenSendAsyncFailed()
                        : annotation.retryTimesWhenSendAsyncFailed());
        producer.setMaxMessageSize(
                annotation.maxMessageSize() == -1 ? producerConfig.getMaxMessageSize() : annotation.maxMessageSize());
        producer.setCompressMsgBodyOverHowmuch(
                annotation.compressMessageBodyThreshold() == -1 ? producerConfig.getCompressMessageBodyThreshold()
                        : annotation.compressMessageBodyThreshold());
        producer.setRetryAnotherBrokerWhenNotStoreOK(annotation.retryNextServer());

        return producer;
    }

    private void validate(final ExtRocketMQTemplateConfiguration annotation,
            final GenericApplicationContext genericApplicationContext) {
        if (genericApplicationContext.isBeanNameInUse(annotation.value())) {
            throw new BeanDefinitionValidationException(String.format("Bean {} has been used in Spring Application Context, " +
                            "please check the @ExtRocketMQTemplateConfiguration",
                    annotation.value()));
        }

        if (rocketMQProperties.getNameServer() == null ||
                rocketMQProperties.getNameServer().equals(environment.resolvePlaceholders(annotation.nameServer()))) {
            throw new BeanDefinitionValidationException(
                    "Bad annotation definition in @ExtRocketMQTemplateConfiguration, nameServer property is same with " +
                            "global property, please use the default RocketMQTemplate!");
        }
    }
}