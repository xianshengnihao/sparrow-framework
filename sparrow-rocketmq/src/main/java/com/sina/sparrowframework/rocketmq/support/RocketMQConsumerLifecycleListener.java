package com.sina.sparrowframework.rocketmq.support;

public interface RocketMQConsumerLifecycleListener<T> {
    void prepareStart(final T consumer);
}
