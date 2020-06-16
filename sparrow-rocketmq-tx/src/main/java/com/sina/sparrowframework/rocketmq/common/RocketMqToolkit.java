package com.sina.sparrow.rocketmq.common;

import org.springframework.beans.BeanUtils;

public class RocketMqToolkit {

    /** {@link com.sina.sparrow.rocketmq.common.MixSendResult}
     * @param sendResult
     * @return
     */
    public static MixSendResult convert(Object sendResult){
        MixSendResult mixSendResult = new MixSendResult();
        BeanUtils.copyProperties(sendResult , mixSendResult);
        return mixSendResult;
    }
}
