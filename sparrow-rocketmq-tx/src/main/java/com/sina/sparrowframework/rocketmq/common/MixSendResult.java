package com.sina.sparrow.rocketmq.common;

import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.SendResult;

import java.io.Serializable;

public class MixSendResult extends SendResult implements Serializable {

    private LocalTransactionState localTransactionState;

    public MixSendResult() {
    }

    public LocalTransactionState getLocalTransactionState() {
        return localTransactionState;
    }

    public void setLocalTransactionState(LocalTransactionState localTransactionState) {
        this.localTransactionState = localTransactionState;
    }

}
