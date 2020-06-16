/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sina.sparrow.rocketmq.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sina.sparrow.core.constants.GlobalConstant;
import com.sina.sparrow.core.utility.StringToolkit;
import com.sina.sparrow.rocketmq.annotation.ConsumeMode;
import com.sina.sparrow.rocketmq.annotation.MessageModel;
import com.sina.sparrow.rocketmq.annotation.RocketMQMessageListener;
import com.sina.sparrow.rocketmq.core.RocketMQListener;
import com.sina.sparrow.rocketmq.support.DefaultRocketMQListenerContainer;
import org.apache.rocketmq.client.AccessChannel;
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
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;


@Configuration
public class ListenerContainerConfiguration implements ApplicationContextAware, SmartInitializingSingleton {
    private final static Logger log = LoggerFactory.getLogger(ListenerContainerConfiguration.class);

    private ConfigurableApplicationContext applicationContext;

    private AtomicLong counter = new AtomicLong(0);

    private StandardEnvironment environment;

    private RocketMQProperties rocketMQProperties;

    private ObjectMapper objectMapper;

    private static final String SPLIT = "_";


    public ListenerContainerConfiguration(ObjectMapper rocketMQMessageObjectMapper,
                                          StandardEnvironment environment,
                                          RocketMQProperties rocketMQProperties) {
        this.objectMapper = rocketMQMessageObjectMapper;
        this.environment = environment;
        this.rocketMQProperties = rocketMQProperties;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }

    @Override
    public void afterSingletonsInstantiated() {
        Map<String, Object> beans = this.applicationContext.getBeansWithAnnotation(RocketMQMessageListener.class);

        if (Objects.nonNull(beans)) {
            beans.forEach(this::registerContainer);
        }
    }

    private void registerContainer(String beanName, Object bean) {
        Class<?> clazz = AopProxyUtils.ultimateTargetClass(bean);

        if (!RocketMQListener.class.isAssignableFrom(bean.getClass())) {
            throw new IllegalStateException(clazz + " is not instance of " + RocketMQListener.class.getName());
        }

        RocketMQMessageListener annotation = clazz.getAnnotation(RocketMQMessageListener.class);
        validate(annotation);

        String containerBeanName = String.format("%s_%s", DefaultRocketMQListenerContainer.class.getName(),
            counter.incrementAndGet());
        GenericApplicationContext genericApplicationContext = (GenericApplicationContext) applicationContext;

        genericApplicationContext.registerBean(containerBeanName, DefaultRocketMQListenerContainer.class,
            () -> createRocketMQListenerContainer(containerBeanName, bean, annotation));
        DefaultRocketMQListenerContainer container = genericApplicationContext.getBean(containerBeanName,
            DefaultRocketMQListenerContainer.class);
        if (!container.isRunning()) {
            try {
                container.start();
            } catch (Exception e) {
                log.error("Started container failed. {}", container, e);
                throw new RuntimeException(e);
            }
        }

        log.info("Register the listener to container, listenerBeanName:{}, containerBeanName:{}", beanName, containerBeanName);
    }

    private DefaultRocketMQListenerContainer createRocketMQListenerContainer(String name, Object bean, RocketMQMessageListener annotation) {
        DefaultRocketMQListenerContainer container = new DefaultRocketMQListenerContainer();

        String nameServer = environment.resolvePlaceholders(annotation.nameServer());
        nameServer = StringUtils.isEmpty(nameServer) ? rocketMQProperties.getNameServer() : nameServer;
        String accessChannel = environment.resolvePlaceholders(annotation.accessChannel());
        container.setNameServer(nameServer);
        if (!StringUtils.isEmpty(accessChannel)) {
            container.setAccessChannel(AccessChannel.valueOf(accessChannel));
        }
        container.setTopic(environment.resolvePlaceholders(annotation.topic()));
        container.setConsumerGroup(buildConsumerGroup(annotation));
        container.setRocketMQMessageListener(annotation);
        container.setRocketMQListener((RocketMQListener) bean);
        container.setObjectMapper(objectMapper);
        container.setName(name);  // REVIEW ME, use the same clientId or multiple?

        return container;
    }

    private void validate(RocketMQMessageListener annotation) {
        if (annotation.consumeMode() == ConsumeMode.ORDERLY &&
            annotation.messageModel() == MessageModel.BROADCASTING) {
            throw new BeanDefinitionValidationException(
                "Bad annotation definition in @RocketMQMessageListener, messageModel BROADCASTING does not support ORDERLY message!");
        }
    }



    private String buildConsumerGroup(RocketMQMessageListener annotation){
        String consumerGroup = environment.resolvePlaceholders(annotation.consumerGroup());
        String topic = environment.resolvePlaceholders(annotation.topic());
        String selectorExpression = environment.resolvePlaceholders(annotation.selectorExpression());
        Assert.notNull(consumerGroup , "createRocketMQListenerContainer consumerGroup can not be null.");
        Assert.notNull(topic , "createRocketMQListenerContainer topic can not be null.");
        StringBuilder target = null;
        if (StringToolkit.isNotBlank(selectorExpression) && !"*".equals(selectorExpression.trim())) {
            String[] tags = selectorExpression.split("\\|\\|");
            target = new StringBuilder();
            target.append(GlobalConstant.UNDERSCORE);
            int length = tags.length;
            for (int i =0 ; i< length ; i++) {
                String tag = tags[i].trim();
                if (i == length - 1 ) {
                    target.append(tag);
                }else {
                    target.append(tag).append(GlobalConstant.UNDERSCORE);
                }
            }
        }
        String groupName = consumerGroup
                .concat(GlobalConstant.UNDERSCORE)
                .concat(topic)
                .concat(target == null ? "" : target.toString());

        log.info("Register Group:{}." , groupName);
        return groupName;
    }


    private String buildConsumerGroup2(RocketMQMessageListener annotation){
        return environment.resolvePlaceholders(annotation.consumerGroup());
    }
}
