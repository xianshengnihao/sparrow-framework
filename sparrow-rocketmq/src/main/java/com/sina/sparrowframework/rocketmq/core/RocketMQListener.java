package com.sina.sparrowframework.rocketmq.core;

public interface RocketMQListener<T> {
    void onMessage(T message);
}
