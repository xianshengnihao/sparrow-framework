package com.sina.sparrowframework.rocketmq.core;

import com.sina.sparrowframework.rocketmq.support.RocketMQConsumerLifecycleListener;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;

public interface RocketMQPushConsumerLifecycleListener extends RocketMQConsumerLifecycleListener<DefaultMQPushConsumer> {
}
