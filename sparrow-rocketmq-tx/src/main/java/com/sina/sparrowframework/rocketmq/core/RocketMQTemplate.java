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

package com.sina.sparrow.rocketmq.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sina.sparrow.core.metadata.tuple.Triple;
import com.sina.sparrow.core.utility.GlobalUtils;
import com.sina.sparrow.rocketmq.annotation.ConsumeMode;
import com.sina.sparrow.rocketmq.common.Constant;
import com.sina.sparrow.rocketmq.config.RocketMQConfigUtils;
import com.sina.sparrow.rocketmq.db.MessageTransaction;
import com.sina.sparrow.rocketmq.db.LocalTransactionService;
import com.sina.sparrow.rocketmq.support.RocketMQUtil;
import org.apache.rocketmq.client.Validators;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.impl.factory.MQClientInstance;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.client.producer.selector.SelectMessageQueueByHash;
import org.apache.rocketmq.common.message.*;
import org.apache.rocketmq.common.protocol.header.EndTransactionRequestHeader;
import org.apache.rocketmq.common.sysflag.MessageSysFlag;
import org.apache.rocketmq.remoting.RPCHook;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.core.AbstractMessageSendingTemplate;
import org.springframework.messaging.core.MessagePostProcessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.Assert;
import org.springframework.util.MimeTypeUtils;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

@SuppressWarnings({"WeakerAccess", "unused"})
public class RocketMQTemplate extends AbstractMessageSendingTemplate<String> implements InitializingBean, DisposableBean , EnvironmentAware {
    private static final  Logger log = LoggerFactory.getLogger(RocketMQTemplate.class);

    private DefaultMQProducer defaultMQProducer;

    private ObjectMapper objectMapper;

    private MQClientInstance mQClientFactory;

    @Autowired
    private LocalTransactionService localTransactionService;

    private TransactionMQProducer txProducer;

    private Environment environment;


    private String charset = "UTF-8";

    private MessageQueueSelector messageQueueSelector = new SelectMessageQueueByHash();

    private final Map<String, TransactionMQProducer> cache = new ConcurrentHashMap<>(); //only put TransactionMQProducer by now!!!

    public DefaultMQProducer getDefaultMQProducer() {
        return defaultMQProducer;
    }

    public void setDefaultMQProducer(DefaultMQProducer defaultMQProducer) {
        this.defaultMQProducer = defaultMQProducer;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public MessageQueueSelector getMessageQueueSelector() {
        return messageQueueSelector;
    }

    public void setMessageQueueSelector(MessageQueueSelector messageQueueSelector) {
        this.messageQueueSelector = messageQueueSelector;
    }

    /**
     * <p> Send message in synchronous mode. This method returns only when the sending procedure totally completes.
     * Reliable synchronous transmission is used in extensive scenes, such as important notification messages, SMS
     * notification, SMS marketing system, etc.. </p>
     * <p>
     * <strong>Warn:</strong> this method has internal retry-mechanism, that is, internal implementation will retry
     * {@link DefaultMQProducer#getRetryTimesWhenSendFailed} times before claiming failure. As a result, multiple
     * messages may potentially delivered to broker(s). It's up to the application developers to resolve potential
     * duplication issue.
     *
     * @param destination formats: `topicName:tags`
     * @param message     {@link org.springframework.messaging.Message}
     * @return {@link SendResult}
     */
    public SendResult syncSend(String destination, Message<?> message) {
        return syncSend(destination, message, defaultMQProducer.getSendMsgTimeout());
    }

    /**
     * Same to {@link #syncSend(String, Message)} with send timeout specified in addition.
     *
     * @param destination formats: `topicName:tags`
     * @param message     {@link org.springframework.messaging.Message}
     * @param timeout     send timeout with millis
     * @return {@link SendResult}
     */
    public SendResult syncSend(String destination, Message<?> message, long timeout) {
        return syncSend(destination, message, timeout, 0);
    }

    /**
     * syncSend batch messages in a given timeout.
     *
     * @param destination formats: `topicName:tags`
     * @param messages    Collection of {@link org.springframework.messaging.Message}
     * @param timeout     send timeout with millis
     * @return {@link SendResult}
     */
    public SendResult syncSend(String destination, Collection<Message<?>> messages, long timeout) {
        if (Objects.isNull(messages) || messages.size() == 0) {
            log.error("syncSend with batch failed. destination:{}, messages is empty ", destination);
            throw new IllegalArgumentException("`messages` can not be empty");
        }

        try {
            long now = System.currentTimeMillis();
            Collection<org.apache.rocketmq.common.message.Message> rmqMsgs = new ArrayList<>();
            org.apache.rocketmq.common.message.Message rocketMsg;
            for (Message<?> msg:messages) {
                if (Objects.isNull(msg) || Objects.isNull(msg.getPayload())) {
                    log.warn("Found a message empty in the batch, skip it");
                    continue;
                }
                rocketMsg = RocketMQUtil.convertToRocketMessage(objectMapper, charset, destination, msg);
                rmqMsgs.add(rocketMsg);
            }

            SendResult sendResult = defaultMQProducer.send(rmqMsgs, timeout);
            long costTime = System.currentTimeMillis() - now;
            log.debug("send messages cost: {} ms, msgId:{}", costTime, sendResult.getMsgId());
            return sendResult;
        } catch (Exception e) {
            log.error("syncSend with batch failed. destination:{}, messages.size:{} ", destination, messages.size());
            throw new MessagingException(e.getMessage(), e);
        }
    }

    /**
     * Same to {@link #syncSend(String, Message)} with send timeout specified in addition.
     *
     * @param destination formats: `topicName:tags`
     * @param message     {@link org.springframework.messaging.Message}
     * @param timeout     send timeout with millis
     * @param delayLevel  level for the delay message
     * @return {@link SendResult}
     */
    private SendResult syncSend(String destination, Message<?> message, long timeout, int delayLevel) {
        if (Objects.isNull(message) || Objects.isNull(message.getPayload())) {
            log.error("syncSend failed. destination:{}, message is null ", destination);
            throw new IllegalArgumentException("`message` and `message.payload` cannot be null");
        }

        try {
            long now = System.currentTimeMillis();
            org.apache.rocketmq.common.message.Message rocketMsg = RocketMQUtil.convertToRocketMessage(objectMapper,
                charset, destination, message);
            if (delayLevel > 0) {
                rocketMsg.setDelayTimeLevel(delayLevel);
            }
            SendResult sendResult = defaultMQProducer.send(rocketMsg, timeout);
            long costTime = System.currentTimeMillis() - now;
            log.debug("send message cost: {} ms, msgId:{}", costTime, sendResult.getMsgId());
            return sendResult;
        } catch (Exception e) {
            log.error("syncSend failed. destination:{}, message:{} ", destination, message);
            throw new MessagingException(e.getMessage(), e);
        }
    }

    /**
     * Same to {@link #syncSend(String, Message)}.
     *
     * @param destination formats: `topicName:tags`
     * @param payload     the Object to use as payload
     * @return {@link SendResult}
     */
    public SendResult syncSend(String destination, Object payload) {
        return syncSend(destination, payload, defaultMQProducer.getSendMsgTimeout());
    }

    /**
     * Same to {@link #syncSend(String, Object)} with send timeout specified in addition.
     *
     * @param destination formats: `topicName:tags`
     * @param payload     the Object to use as payload
     * @param timeout     send timeout with millis
     * @return {@link SendResult}
     */
    public SendResult syncSend(String destination, Object payload, long timeout) {
        Message<?> message = this.doConvert(payload, null, null);
        return syncSend(destination, message, timeout);
    }

    /**
     * Same to {@link #syncSend(String, Message)} with send orderly with hashKey by specified.
     *
     * @param destination formats: `topicName:tags`
     * @param message     {@link org.springframework.messaging.Message}
     * @param hashKey     use this key to select queue. for example: orderId, productId ...
     * @return {@link SendResult}
     */
    public SendResult syncSendOrderly(String destination, Message<?> message, String hashKey) {
        return syncSendOrderly(destination, message, hashKey, defaultMQProducer.getSendMsgTimeout());
    }

    /**
     * Same to {@link #syncSendOrderly(String, Message, String)} with send timeout specified in addition.
     *
     * @param destination formats: `topicName:tags`
     * @param message     {@link org.springframework.messaging.Message}
     * @param hashKey     use this key to select queue. for example: orderId, productId ...
     * @param timeout     send timeout with millis
     * @return {@link SendResult}
     */
    public SendResult syncSendOrderly(String destination, Message<?> message, String hashKey, long timeout) {
        if (Objects.isNull(message) || Objects.isNull(message.getPayload())) {
            log.error("syncSendOrderly failed. destination:{}, message is null ", destination);
            throw new IllegalArgumentException("`message` and `message.payload` cannot be null");
        }

        try {
            long now = System.currentTimeMillis();
            org.apache.rocketmq.common.message.Message rocketMsg = RocketMQUtil.convertToRocketMessage(objectMapper,
                charset, destination, message);
            SendResult sendResult = defaultMQProducer.send(rocketMsg, messageQueueSelector, hashKey, timeout);
            long costTime = System.currentTimeMillis() - now;
            log.debug("send message cost: {} ms, msgId:{}", costTime, sendResult.getMsgId());
            return sendResult;
        } catch (Exception e) {
            log.error("syncSendOrderly failed. destination:{}, message:{} ", destination, message);
            throw new MessagingException(e.getMessage(), e);
        }
    }

    /**
     * Same to {@link #syncSend(String, Object)} with send orderly with hashKey by specified.
     *
     * @param destination formats: `topicName:tags`
     * @param payload     the Object to use as payload
     * @param hashKey     use this key to select queue. for example: orderId, productId ...
     * @return {@link SendResult}
     */
    public SendResult syncSendOrderly(String destination, Object payload, String hashKey) {
        return syncSendOrderly(destination, payload, hashKey, defaultMQProducer.getSendMsgTimeout());
    }

    /**
     * Same to {@link #syncSendOrderly(String, Object, String)} with send timeout specified in addition.
     *
     * @param destination formats: `topicName:tags`
     * @param payload     the Object to use as payload
     * @param hashKey     use this key to select queue. for example: orderId, productId ...
     * @param timeout     send timeout with millis
     * @return {@link SendResult}
     */
    public SendResult syncSendOrderly(String destination, Object payload, String hashKey, long timeout) {
        Message<?> message = this.doConvert(payload, null, null);
        return syncSendOrderly(destination, message, hashKey, defaultMQProducer.getSendMsgTimeout());
    }
    /**
     *
     * @param destination  formats: `topicName:tags`
     * @param message      {@link org.springframework.messaging.Message}
     * @param sendCallback {@link SendCallback}
     * @param timeout      send timeout with millis
     * @param delayLevel   level for the delay message
     */
    public void asyncSend(String destination, Message<?> message, SendCallback sendCallback, long timeout, int delayLevel) {
        if (Objects.isNull(message) || Objects.isNull(message.getPayload())) {
            log.error("asyncSend failed. destination:{}, message is null ", destination);
            throw new IllegalArgumentException("`message` and `message.payload` cannot be null");
        }

        try {
            org.apache.rocketmq.common.message.Message rocketMsg = RocketMQUtil.convertToRocketMessage(objectMapper,
                charset, destination, message);
            if (delayLevel > 0) {
                rocketMsg.setDelayTimeLevel(delayLevel);
            }
            defaultMQProducer.send(rocketMsg, sendCallback, timeout);
        } catch (Exception e) {
            log.info("asyncSend failed. destination:{}, message:{} ", destination, message);
            throw new MessagingException(e.getMessage(), e);
        }
    }
    /***
     * @param destination  formats: `topicName:tags`
     * @param message      {@link org.springframework.messaging.Message}
     * @param sendCallback {@link SendCallback}
     * @param timeout      send timeout with millis
     */
    private void asyncSend(String destination, Message<?> message, SendCallback sendCallback, long timeout) {
        asyncSend(destination,message,sendCallback,timeout,0);
    }


    /**
     * Same to {@link #asyncSend(String, Object, SendCallback)} with send timeout specified in addition.
     *
     * @param destination  formats: `topicName:tags`
     * @param payload      the Object to use as payload
     * @param sendCallback {@link SendCallback}
     * @param timeout      send timeout with millis
     */
    private void asyncSend(String destination, Object payload, SendCallback sendCallback, long timeout) {
        Message<?> message = this.doConvert(payload, null, null);
        asyncSend(destination, message, sendCallback, timeout);
    }

    /**
     *
     * @param destination  formats: `topicName:tags`
     * @param payload      the Object to use as payload
     * @param sendCallback {@link SendCallback}
     */
    public void asyncSend(String destination, Object payload, SendCallback sendCallback) {
        asyncSend(destination, payload, sendCallback, defaultMQProducer.getSendMsgTimeout());
    }


    /**
     * Similar to <a href="https://en.wikipedia.org/wiki/User_Datagram_Protocol">UDP</a>, this method won't wait for
     * acknowledgement from broker before return. Obviously, it has maximums throughput yet potentials of message loss.
     * <p>
     * One-way transmission is used for cases requiring moderate reliability, such as log collection.
     *
     * @param destination formats: `topicName:tags`
     * @param message     {@link org.springframework.messaging.Message}
     */
    private void sendOneWay(String destination, Message<?> message) {
        if (Objects.isNull(message) || Objects.isNull(message.getPayload())) {
            log.error("sendOneWay failed. destination:{}, message is null ", destination);
            throw new IllegalArgumentException("`message` and `message.payload` cannot be null");
        }

        try {
            org.apache.rocketmq.common.message.Message rocketMsg = RocketMQUtil.convertToRocketMessage(objectMapper,
                charset, destination, message);
            defaultMQProducer.sendOneway(rocketMsg);
        } catch (Exception e) {
            log.error("sendOneWay failed. destination:{}, message:{} ", destination, message);
            throw new MessagingException(e.getMessage(), e);
        }
    }

    /**
     * Same to {@link #sendOneWay(String, Message)}
     *
     * @param destination formats: `topicName:tags`
     * @param payload     the Object to use as payload
     */
    public void sendOneWay(String destination, Object payload) {
        Message<?> message = this.doConvert(payload, null, null);
        sendOneWay(destination, message);
    }

    /**
     * Same to {@link #sendOneWay(String, Message)} with send orderly with hashKey by specified.
     *
     * @param destination formats: `topicName:tags`
     * @param message     {@link org.springframework.messaging.Message}
     * @param hashKey     use this key to select queue. for example: orderId, productId ...
     */
    private void sendOneWayOrderly(String destination, Message<?> message, String hashKey) {
        if (Objects.isNull(message) || Objects.isNull(message.getPayload())) {
            log.error("sendOneWayOrderly failed. destination:{}, message is null ", destination);
            throw new IllegalArgumentException("`message` and `message.payload` cannot be null");
        }

        try {
            org.apache.rocketmq.common.message.Message rocketMsg = RocketMQUtil.convertToRocketMessage(objectMapper,
                charset, destination, message);
            defaultMQProducer.sendOneway(rocketMsg, messageQueueSelector, hashKey);
        } catch (Exception e) {
            log.error("sendOneWayOrderly failed. destination:{}, message:{}", destination, message);
            throw new MessagingException(e.getMessage(), e);
        }
    }

    /**
     * Same to {@link #sendOneWayOrderly(String, Message, String)}
     *
     * @param destination formats: `topicName:tags`
     * @param payload     the Object to use as payload
     */
    public void sendOneWayOrderly(String destination, Object payload, String hashKey) {
        Message<?> message = this.doConvert(payload, null, null);
        sendOneWayOrderly(destination, message, hashKey);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (defaultMQProducer != null) {
            defaultMQProducer.start();
        }
        mQClientFactory = this.defaultMQProducer.getDefaultMQProducerImpl().getmQClientFactory();
    }

    @Override
    protected void doSend(String destination, Message<?> message) {
        SendResult sendResult = syncSend(destination, message);
        log.debug("send message to `{}` finished. result:{}", destination, sendResult);
    }



    @Override
    protected Message<?> doConvert(Object payload, Map<String, Object> headers, MessagePostProcessor postProcessor) {
        String content;
        if (payload instanceof String) {
            content = (String) payload;
        } else {
            // If payload not as string, use objectMapper change it.
            try {
                content = objectMapper.writeValueAsString(payload);
            } catch (JsonProcessingException e) {
                log.error("convert payload to String failed. payload:{}", payload);
                throw new RuntimeException("convert to payload to String failed.", e);
            }
        }

        MessageBuilder<?> builder = MessageBuilder.withPayload(content);
        if (headers != null) {
            builder.copyHeaders(headers);
        }
        builder.setHeaderIfAbsent(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.TEXT_PLAIN);

        Message<?> message = builder.build();
        if (postProcessor != null) {
            message = postProcessor.postProcessMessage(message);
        }
        return message;
    }

    @Override
    public void destroy() {
        if (Objects.nonNull(defaultMQProducer)) {
            defaultMQProducer.shutdown();
        }

        for (Map.Entry<String, TransactionMQProducer> kv : cache.entrySet()) {
            if (Objects.nonNull(kv.getValue())) {
                kv.getValue().shutdown();
            }
        }
        cache.clear();
    }

    private String getTxProducerGroupName(String name) {
        return name == null ? RocketMQConfigUtils.ROCKETMQ_TRANSACTION_DEFAULT_GLOBAL_NAME : name;
    }

    private TransactionMQProducer stageMQProducer(String name) throws MessagingException {
        name = getTxProducerGroupName(name);

        TransactionMQProducer cachedProducer = cache.get(name);
        if (cachedProducer == null) {
            throw new MessagingException(
                String.format("Can not found MQProducer '%s' in cache! please define @RocketMQLocalTransactionListener class or invoke createOrGetStartedTransactionMQProducer() to create it firstly", name));
        }

        return cachedProducer;
    }


    private TransactionMQProducer stageMQProducerDefault() throws MessagingException {
        return stageMQProducer(getTxProducerGroupName(Constant.txProducerGroup));
    }



    /**
     * Send Spring Message in Transaction
     *
     * @param destination     destination formats: `topicName:tags`
     * @param payload
     * @return TransactionSendResult
     * @throws MessagingException
     */
    public TransactionSendResult sendInTransaction(final String destination, Object payload ) throws MessagingException {
        try {
            Message<?> message = this.doConvert(payload, null, null);
            org.apache.rocketmq.common.message.Message rocketMsg = RocketMQUtil.convertToRocketMessage(objectMapper, charset, destination, message);
            TransactionSendResult transactionSendResult = this.sendMessageInTransaction(rocketMsg, ConsumeMode.CONCURRENTLY, null);
            /**
             * rocketMsg发送成功后服务端会赋值transactionId
             * {@see org.apache.rocketmq.client.impl.producer.DefaultMQProducerImpl#sendKernelImpl(org.apache.rocketmq.common.message.Message, org.apache.rocketmq.common.message.MessageQueue, org.apache.rocketmq.client.impl.CommunicationMode, org.apache.rocketmq.client.producer.SendCallback, org.apache.rocketmq.client.impl.producer.TopicPublishInfo, long)}
             */
            Assert.notNull(rocketMsg.getTransactionId() , "rocketMsg transactionId Normal_Msg can not be null." );
            localTransactionService.log(new MessageTransaction().setTransactionId(rocketMsg.getTransactionId()));

            return transactionSendResult;
        } catch (MQClientException e) {
            throw RocketMQUtil.convert(e);
        }
    }

    /**
     * Send Spring Message in Transaction
     *
     * @param destination     destination formats: `topicName:tags`
     * @param payload
     * @return TransactionSendResult
     * @throws MessagingException
     */
    @Deprecated
    protected TransactionSendResult sendInTransactionOrderly(final String destination, Object payload , String hashKey) throws MessagingException {
        try {
            Message<?> message = this.doConvert(payload, null, null);
            org.apache.rocketmq.common.message.Message rocketMsg = RocketMQUtil.convertToRocketMessage(objectMapper, charset, destination, message);
            TransactionSendResult transactionSendResult = this.sendMessageInTransaction(rocketMsg, ConsumeMode.ORDERLY, hashKey);

            /**
             * rocketMsg发送成功后服务端会赋值transactionId
             * {@see org.apache.rocketmq.client.impl.producer.DefaultMQProducerImpl#sendKernelImpl(org.apache.rocketmq.common.message.Message, org.apache.rocketmq.common.message.MessageQueue, org.apache.rocketmq.client.impl.CommunicationMode, org.apache.rocketmq.client.producer.SendCallback, org.apache.rocketmq.client.impl.producer.TopicPublishInfo, long)}
             */
            Assert.notNull(rocketMsg.getTransactionId() , "rocketMsg transactionId Trans_Msg_Half can not be null." );
            localTransactionService.log(new MessageTransaction().setTransactionId(rocketMsg.getTransactionId()));

            return transactionSendResult;
        } catch (MQClientException e) {
            throw RocketMQUtil.convert(e);
        }
    }


    /**
     * Remove a TransactionMQProducer from cache by manual.
     * <p>Note: RocketMQTemplate can release all cached producers when bean destroying, it is not recommended to directly
     * use this method by user.
     *
     * @param txProducerGroup
     * @throws MessagingException
     */
    public void removeTransactionMQProducer(String txProducerGroup) throws MessagingException {
        txProducerGroup = getTxProducerGroupName(txProducerGroup);
        if (cache.containsKey(txProducerGroup)) {
            DefaultMQProducer cachedProducer = cache.get(txProducerGroup);
            cachedProducer.shutdown();
            cache.remove(txProducerGroup);
        }
    }

    /**
     * Create and start a transaction MQProducer, this new producer is cached in memory.
     * <p>Note: This method is invoked internally when processing {@code @RocketMQLocalTransactionListener}, it is not
     * recommended to directly use this method by user.
     *
     * @param txProducerGroup     Producer (group) name, unique for each producer
     * @param transactionListener TransactoinListener impl class
     * @param executorService     Nullable.
     * @param rpcHook Nullable.
     * @return true if producer is created and started; false if the named producer already exists in cache.
     * @throws MessagingException
     */
    public boolean createAndStartTransactionMQProducer(String txProducerGroup,
                                                       RocketMQLocalTransactionListener transactionListener,
                                                       ExecutorService executorService, RPCHook rpcHook) throws MessagingException {
        txProducerGroup = getTxProducerGroupName(txProducerGroup);
        if (cache.containsKey(txProducerGroup)) {
            log.info(String.format("get TransactionMQProducer '%s' from cache", txProducerGroup));
            return false;
        }

        TransactionMQProducer txProducer = createTransactionMQProducer(txProducerGroup, transactionListener, executorService, rpcHook);
        try {
            txProducer.start();
            cache.put(txProducerGroup, txProducer);
        } catch (MQClientException e) {
            throw RocketMQUtil.convert(e);
        }

        return true;
    }

    private TransactionMQProducer createTransactionMQProducer(String name,
                                                              RocketMQLocalTransactionListener transactionListener,
                                                              ExecutorService executorService, RPCHook rpcHook) {
        Assert.notNull(defaultMQProducer, "Property 'producer' is required");
        Assert.notNull(transactionListener, "Parameter 'transactionListener' is required");
        TransactionMQProducer txProducer;
        if (Objects.nonNull(rpcHook)) {
            txProducer = new TransactionMQProducer(name, rpcHook);
            txProducer.setVipChannelEnabled(false);
            txProducer.setInstanceName(RocketMQUtil.getInstanceName(rpcHook, name));
        } else {
            txProducer = new TransactionMQProducer(name);
        }
        txProducer.setTransactionListener(RocketMQUtil.convert(transactionListener));

        txProducer.setNamesrvAddr(defaultMQProducer.getNamesrvAddr());
        if (executorService != null) {
            txProducer.setExecutorService(executorService);
        }

        txProducer.setSendMsgTimeout(defaultMQProducer.getSendMsgTimeout());
        txProducer.setRetryTimesWhenSendFailed(defaultMQProducer.getRetryTimesWhenSendFailed());
        txProducer.setRetryTimesWhenSendAsyncFailed(defaultMQProducer.getRetryTimesWhenSendAsyncFailed());
        txProducer.setMaxMessageSize(defaultMQProducer.getMaxMessageSize());
        txProducer.setCompressMsgBodyOverHowmuch(defaultMQProducer.getCompressMsgBodyOverHowmuch());
        txProducer.setRetryAnotherBrokerWhenNotStoreOK(defaultMQProducer.isRetryAnotherBrokerWhenNotStoreOK());

        return txProducer;
    }



    private TransactionSendResult sendMessageInTransaction(final org.apache.rocketmq.common.message.Message msg , ConsumeMode consumeMode , Object args)
            throws MQClientException {
        Validators.checkMessage(msg, stageMQProducerDefault());

        SendResult sendResult = null;
        MessageAccessor.putProperty(msg, MessageConst.PROPERTY_TRANSACTION_PREPARED, "true");
        MessageAccessor.putProperty(msg, MessageConst.PROPERTY_PRODUCER_GROUP, this.stageMQProducerDefault().getProducerGroup());
        try {
            if (consumeMode == ConsumeMode.ORDERLY) {
                sendResult = stageMQProducerDefault().send(msg , messageQueueSelector , args);
            }else {
                sendResult = stageMQProducerDefault().send(msg);
            }
        } catch (Exception e) {
            throw new MQClientException("send message Exception", e);
        }

        LocalTransactionState localTransactionState = LocalTransactionState.UNKNOW;
        Throwable localException = null;
        switch (sendResult.getSendStatus()) {
            case SEND_OK: {
                try {
                    if (sendResult.getTransactionId() != null) {
                        msg.putUserProperty("__transactionId__", sendResult.getTransactionId());
                    }
                    String transactionId = msg.getProperty(MessageConst.PROPERTY_UNIQ_CLIENT_MESSAGE_ID_KEYIDX);
                    if (null != transactionId && !"".equals(transactionId)) {
                        msg.setTransactionId(transactionId);
                    }
                } catch (Throwable e) {
                    log.info("executeLocalTransactionBranch exception", e);
                    log.info(msg.toString());
                    localException = e;
                }
            }
            break;
            case FLUSH_DISK_TIMEOUT:
            case FLUSH_SLAVE_TIMEOUT:
            case SLAVE_NOT_AVAILABLE:
                localTransactionState = LocalTransactionState.ROLLBACK_MESSAGE;
                break;
            default:
                break;
        }

        SendResult finalSendResult = sendResult;
        LocalTransactionState finalLocalTransactionState = localTransactionState;

        CurrentSendMqHolder.setTriples(new Triple<Boolean, SendResult, LocalTransactionState>() {
            @Override
            public Boolean getLeft() {
                return Boolean.TRUE;
            }

            @Override
            public SendResult getMiddle() {
                return finalSendResult;
            }

            @Override
            public LocalTransactionState getRight() {
                return finalLocalTransactionState;
            }
        });

        TransactionSendResult transactionSendResult = new TransactionSendResult();
        transactionSendResult.setSendStatus(sendResult.getSendStatus());
        transactionSendResult.setMessageQueue(sendResult.getMessageQueue());
        transactionSendResult.setMsgId(sendResult.getMsgId());
        transactionSendResult.setQueueOffset(sendResult.getQueueOffset());
        transactionSendResult.setTransactionId(sendResult.getTransactionId());
        transactionSendResult.setLocalTransactionState(localTransactionState);
        return transactionSendResult;
    }

    /**
     * DEFAULT SYNC -------------------------------------------------------
     */
    private SendResult send(
            org.apache.rocketmq.common.message.Message msg) throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
        return this.defaultMQProducer.getDefaultMQProducerImpl().send(msg, this.defaultMQProducer.getSendMsgTimeout());
    }

    /**
     * 结束事务发送
     * @param sendResult
     * @param localTransactionState
     * @throws RemotingException
     * @throws MQBrokerException
     * @throws InterruptedException
     * @throws UnknownHostException
     */
    protected void endTransaction(final SendResult sendResult, final LocalTransactionState localTransactionState)
            throws RemotingException, MQBrokerException, InterruptedException, UnknownHostException
    {
        final MessageId id;
        if (sendResult.getOffsetMsgId() != null) {
            id = MessageDecoder.decodeMessageId(sendResult.getOffsetMsgId());
        } else {
            id = MessageDecoder.decodeMessageId(sendResult.getMsgId());
        }
        String transactionId = sendResult.getTransactionId();
        final String brokerAddr = this.stageMQProducerDefault().getDefaultMQProducerImpl().getmQClientFactory().
                findBrokerAddressInPublish(sendResult.getMessageQueue().getBrokerName());
        EndTransactionRequestHeader requestHeader = new EndTransactionRequestHeader();
        requestHeader.setTransactionId(transactionId);
        requestHeader.setCommitLogOffset(id.getOffset());
        switch (localTransactionState) {
            case COMMIT_MESSAGE:
                requestHeader.setCommitOrRollback(MessageSysFlag.TRANSACTION_COMMIT_TYPE);
                break;
            case ROLLBACK_MESSAGE:
                requestHeader.setCommitOrRollback(MessageSysFlag.TRANSACTION_ROLLBACK_TYPE);
                break;
            case UNKNOW:
                requestHeader.setCommitOrRollback(MessageSysFlag.TRANSACTION_NOT_TYPE);
                break;
            default:
                break;
        }

        requestHeader.setProducerGroup(stageMQProducerDefault().getProducerGroup());
        requestHeader.setTranStateTableOffset(sendResult.getQueueOffset());
        requestHeader.setMsgId(sendResult.getMsgId());
        this.stageMQProducerDefault().getDefaultMQProducerImpl().getmQClientFactory().getMQClientAPIImpl().endTransactionOneway(
                brokerAddr,
                requestHeader,
                "endTransaction defalut error message.",
                this.stageMQProducerDefault().getSendMsgTimeout()
        );
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
