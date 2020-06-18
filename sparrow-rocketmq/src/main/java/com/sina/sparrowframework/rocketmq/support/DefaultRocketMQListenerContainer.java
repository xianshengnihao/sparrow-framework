
package com.sina.sparrowframework.rocketmq.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sina.sparrowframework.rocketmq.annotation.ConsumeMode;
import com.sina.sparrowframework.rocketmq.annotation.MessageModel;
import com.sina.sparrowframework.rocketmq.annotation.RocketMQMessageListener;
import com.sina.sparrowframework.rocketmq.annotation.SelectorType;
import com.sina.sparrowframework.rocketmq.core.RocketMQListener;
import com.sina.sparrowframework.rocketmq.core.RocketMQPushConsumerLifecycleListener;
import com.sina.sparrowframework.tools.utility.Assert;
import org.apache.rocketmq.client.AccessChannel;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MessageSelector;
import org.apache.rocketmq.client.consumer.listener.*;
import org.apache.rocketmq.client.consumer.rebalance.AllocateMessageQueueAveragely;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.RPCHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.SmartLifecycle;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("WeakerAccess")
public class DefaultRocketMQListenerContainer implements InitializingBean, RocketMQListenerContainer, SmartLifecycle, ApplicationContextAware {
    private final static Logger log = LoggerFactory.getLogger(DefaultRocketMQListenerContainer.class);

    private ApplicationContext applicationContext;

    /**
     * The name of the DefaultRocketMQListenerContainer instance
     */
    private String name;

    private long suspendCurrentQueueTimeMillis = 1000;

    /**
     * Message consume retry strategy<br> -1,no retry,put into DLQ directly<br> 0,broker control retry frequency<br>
     * >0,client control retry frequency.
     */
    private int delayLevelWhenNextConsume = 0;

    private String nameServer;

    private AccessChannel accessChannel = AccessChannel.LOCAL;

    private String consumerGroup;

    private String topic;

    private int consumeThreadMax = 64;

    private String charset = "UTF-8";

    private ObjectMapper objectMapper;

    private RocketMQListener rocketMQListener;

    private RocketMQMessageListener rocketMQMessageListener;

    private DefaultMQPushConsumer consumer;

    private Class messageType;

    private boolean running;

    // The following properties came from @RocketMQMessageListener.
    private ConsumeMode consumeMode;
    private SelectorType selectorType;
    private String selectorExpression;
    private MessageModel messageModel;
    private long consumeTimeout;

    public long getSuspendCurrentQueueTimeMillis() {
        return suspendCurrentQueueTimeMillis;
    }

    public void setSuspendCurrentQueueTimeMillis(final long suspendCurrentQueueTimeMillis) {
        this.suspendCurrentQueueTimeMillis = suspendCurrentQueueTimeMillis;
    }

    public int getDelayLevelWhenNextConsume() {
        return delayLevelWhenNextConsume;
    }

    public void setDelayLevelWhenNextConsume(final int delayLevelWhenNextConsume) {
        this.delayLevelWhenNextConsume = delayLevelWhenNextConsume;
    }

    public String getNameServer() {
        return nameServer;
    }

    public void setNameServer(final String nameServer) {
        this.nameServer = nameServer;
    }

    public AccessChannel getAccessChannel() {
        return accessChannel;
    }

    public void setAccessChannel(final AccessChannel accessChannel) {
        this.accessChannel = accessChannel;
    }

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(final String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(final String topic) {
        this.topic = topic;
    }

    public int getConsumeThreadMax() {
        return consumeThreadMax;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(final String charset) {
        this.charset = charset;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void setObjectMapper(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public RocketMQListener getRocketMQListener() {
        return rocketMQListener;
    }

    public void setRocketMQListener(final RocketMQListener rocketMQListener) {
        this.rocketMQListener = rocketMQListener;
    }

    public RocketMQMessageListener getRocketMQMessageListener() {
        return rocketMQMessageListener;
    }

    public void setRocketMQMessageListener(final RocketMQMessageListener anno) {
        this.rocketMQMessageListener = anno;

        this.consumeMode = anno.consumeMode();
        this.consumeThreadMax = anno.consumeThreadMax();
        this.messageModel = anno.messageModel();
        this.selectorExpression = anno.selectorExpression();
        this.selectorType = anno.selectorType();
        this.consumeTimeout = anno.consumeTimeout();
    }

    public ConsumeMode getConsumeMode() {
        return consumeMode;
    }

    public SelectorType getSelectorType() {
        return selectorType;
    }

    public String getSelectorExpression() {
        return selectorExpression;
    }

    public MessageModel getMessageModel() {
        return messageModel;
    }

    public DefaultMQPushConsumer getConsumer() {
        return consumer;
    }

    public void setConsumer(final DefaultMQPushConsumer consumer) {
        this.consumer = consumer;
    }

    @Override
    public void setupMessageListener(final RocketMQListener rocketMQListener) {
        this.rocketMQListener = rocketMQListener;
    }

    @Override
    public void destroy() {
        this.setRunning(false);
        if (Objects.nonNull(consumer)) {
            consumer.shutdown();
        }
        log.info("container destroyed, {}", this.toString());
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(final Runnable callback) {
        stop();
        callback.run();
    }

    @Override
    public void start() {
        if (this.isRunning()) {
            throw new IllegalStateException("container already running. " + this.toString());
        }

        try {
            consumer.start();
        } catch (final MQClientException e) {
            throw new IllegalStateException("Failed to start RocketMQ push consumer", e);
        }
        this.setRunning(true);

        log.info("running container: {}", this.toString());
    }

    @Override
    public void stop() {
        if (this.isRunning()) {
            if (Objects.nonNull(consumer)) {
                consumer.shutdown();
            }
            setRunning(false);
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    private void setRunning(final boolean running) {
        this.running = running;
    }

    @Override
    public int getPhase() {
        // Returning Integer.MAX_VALUE only suggests that
        // we will be the first bean to shutdown and last bean to start
        return Integer.MAX_VALUE;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        initRocketMQPushConsumer();

        this.messageType = getMessageType();
        log.debug("RocketMQ messageType: {}", messageType.getName());
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public String toString() {
        return "DefaultRocketMQListenerContainer{" + "consumerGroup='" + consumerGroup + '\'' + ", nameServer='"
                + nameServer + '\'' + ", topic='" + topic + '\'' + ", consumeMode=" + consumeMode + ", selectorType="
                + selectorType + ", selectorExpression='" + selectorExpression + '\'' + ", messageModel=" + messageModel
                + '}';
    }

    public void setName(final String name) {
        this.name = name;
    }

    public class DefaultMessageListenerConcurrently implements MessageListenerConcurrently {

        @SuppressWarnings("unchecked")
        @Override
        public ConsumeConcurrentlyStatus consumeMessage(final List<MessageExt> msgs,
                final ConsumeConcurrentlyContext context) {
            for (final MessageExt messageExt : msgs) {
                log.debug("received msg: {}", messageExt);
                try {
                    final long now = System.currentTimeMillis();
                    rocketMQListener.onMessage(doConvertMessage(messageExt));
                    final long costTime = System.currentTimeMillis() - now;
                    log.debug("consume {} cost: {} ms", messageExt.getMsgId(), costTime);
                } catch (final Exception e) {
                    log.warn("consume message failed. messageExt:{}", messageExt, e);
                    context.setDelayLevelWhenNextConsume(delayLevelWhenNextConsume);
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
            }

            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
    }

    public class DefaultMessageListenerOrderly implements MessageListenerOrderly {

        @SuppressWarnings("unchecked")
        @Override
        public ConsumeOrderlyStatus consumeMessage(final List<MessageExt> msgs, final ConsumeOrderlyContext context) {
            for (final MessageExt messageExt : msgs) {
                log.debug("received msg: {}", messageExt);
                try {
                    final long now = System.currentTimeMillis();
                    rocketMQListener.onMessage(doConvertMessage(messageExt));
                    final long costTime = System.currentTimeMillis() - now;
                    log.info("consume {} cost: {} ms", messageExt.getMsgId(), costTime);
                } catch (final Exception e) {
                    log.warn("consume message failed. messageExt:{}", messageExt, e);
                    context.setSuspendCurrentQueueTimeMillis(suspendCurrentQueueTimeMillis);
                    return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
                }
            }

            return ConsumeOrderlyStatus.SUCCESS;
        }
    }

    @SuppressWarnings("unchecked")
    private Object doConvertMessage(final MessageExt messageExt) {
        if (Objects.equals(messageType, MessageExt.class)) {
            return messageExt;
        } else {
            final String str = new String(messageExt.getBody(), Charset.forName(charset));
            if (Objects.equals(messageType, String.class)) {
                return str;
            } else {
                // If msgType not string, use objectMapper change it.
                try {
                    return objectMapper.readValue(str, messageType);
                } catch (final Exception e) {
                    log.info("convert failed. str:{}, msgType:{}", str, messageType);
                    throw new RuntimeException("cannot convert message to " + messageType, e);
                }
            }
        }
    }

    private Class getMessageType() {
        final Class<?> targetClass = AopProxyUtils.ultimateTargetClass(rocketMQListener);
        Type[] interfaces = targetClass.getGenericInterfaces();
        Class<?> superclass = targetClass.getSuperclass();
        while ((Objects.isNull(interfaces) || 0 == interfaces.length) && Objects.nonNull(superclass)) {
            interfaces = superclass.getGenericInterfaces();
            superclass = targetClass.getSuperclass();
        }
        if (Objects.nonNull(interfaces)) {
            for (final Type type : interfaces) {
                if (type instanceof ParameterizedType) {
                    final ParameterizedType parameterizedType = (ParameterizedType) type;
                    if (Objects.equals(parameterizedType.getRawType(), RocketMQListener.class)) {
                        final Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                        if (Objects.nonNull(actualTypeArguments) && actualTypeArguments.length > 0) {
                            return (Class) actualTypeArguments[0];
                        } else {
                            return Object.class;
                        }
                    }
                }
            }

            return Object.class;
        } else {
            return Object.class;
        }
    }

    private void initRocketMQPushConsumer() throws MQClientException {
        Assert.notNull(rocketMQListener, "Property 'rocketMQListener' is required");
        Assert.notNull(consumerGroup, "Property 'consumerGroup' is required");
        Assert.notNull(nameServer, "Property 'nameServer' is required");
        Assert.notNull(topic, "Property 'topic' is required");

        final RPCHook rpcHook = RocketMQUtil.getRPCHookByAkSk(applicationContext.getEnvironment(),
                this.rocketMQMessageListener.accessKey(), this.rocketMQMessageListener.secretKey());
        final boolean enableMsgTrace = rocketMQMessageListener.enableMsgTrace();
        if (Objects.nonNull(rpcHook)) {
            consumer = new DefaultMQPushConsumer(consumerGroup, rpcHook, new AllocateMessageQueueAveragely(),
                    enableMsgTrace, this.applicationContext.getEnvironment()
                            .resolveRequiredPlaceholders(this.rocketMQMessageListener.customizedTraceTopic()));
            consumer.setVipChannelEnabled(false);
            consumer.setInstanceName(RocketMQUtil.getInstanceName(rpcHook, consumerGroup));
        } else {
            log.debug("Access-key or secret-key not configure in " + this + ".");
            consumer = new DefaultMQPushConsumer(consumerGroup, enableMsgTrace, this.applicationContext.getEnvironment()
                    .resolveRequiredPlaceholders(this.rocketMQMessageListener.customizedTraceTopic()));
        }

        final String customizedNameServer = this.applicationContext.getEnvironment()
                .resolveRequiredPlaceholders(this.rocketMQMessageListener.nameServer());
        if (customizedNameServer != null) {
            consumer.setNamesrvAddr(customizedNameServer);
        } else {
            consumer.setNamesrvAddr(nameServer);
        }
        if (accessChannel != null) {
            consumer.setAccessChannel(accessChannel);
        }
        consumer.setConsumeThreadMax(consumeThreadMax);
        if (consumeThreadMax < consumer.getConsumeThreadMin()) {
            consumer.setConsumeThreadMin(consumeThreadMax);
        }
        consumer.setConsumeTimeout(consumeTimeout);
        consumer.setInstanceName(this.name);

        switch (messageModel) {
            case BROADCASTING:
                consumer.setMessageModel(org.apache.rocketmq.common.protocol.heartbeat.MessageModel.BROADCASTING);
                break;
            case CLUSTERING:
                consumer.setMessageModel(org.apache.rocketmq.common.protocol.heartbeat.MessageModel.CLUSTERING);
                break;
            default:
                throw new IllegalArgumentException("Property 'messageModel' was wrong.");
        }

        switch (selectorType) {
            case TAG:
                consumer.subscribe(topic, selectorExpression);
                break;
            case SQL92:
                consumer.subscribe(topic, MessageSelector.bySql(selectorExpression));
                break;
            default:
                throw new IllegalArgumentException("Property 'selectorType' was wrong.");
        }

        switch (consumeMode) {
            case ORDERLY:
                consumer.setMessageListener(new DefaultMessageListenerOrderly());
                break;
            case CONCURRENTLY:
                consumer.setMessageListener(new DefaultMessageListenerConcurrently());
                break;
            default:
                throw new IllegalArgumentException("Property 'consumeMode' was wrong.");
        }

        if (rocketMQListener instanceof RocketMQPushConsumerLifecycleListener) {
            ((RocketMQPushConsumerLifecycleListener) rocketMQListener).prepareStart(consumer);
        }

    }

}
