package com.sina.sparrowframework.rocketmq.common;

import org.springframework.beans.BeanUtils;

public class RocketMqToolkit {

    /**
     * 结果集转化
     * @param sendResult
     * @return
     */
    public static MixSendResult convert(final Object sendResult) {
        final MixSendResult mixSendResult = new MixSendResult();
        BeanUtils.copyProperties(sendResult , mixSendResult);
        return mixSendResult;
    }
}
