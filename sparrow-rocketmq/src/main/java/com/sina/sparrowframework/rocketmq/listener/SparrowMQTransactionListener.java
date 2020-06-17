package com.sina.sparrowframework.rocketmq.listener;

import com.sina.sparrowframework.rocketmq.annotation.RocketMQTransactionListener;
import com.sina.sparrowframework.rocketmq.common.Constant;
import com.sina.sparrowframework.rocketmq.core.RocketMQLocalTransactionListener;
import com.sina.sparrowframework.rocketmq.core.RocketMQLocalTransactionState;
import com.sina.sparrowframework.rocketmq.db.LocalTransactionService;
import com.sina.sparrowframework.rocketmq.support.RocketMQHeaders;
import com.sina.sparrowframework.rocketmq.support.RocketMQUtil;
import com.sina.sparrowframework.tools.utility.ObjectToolkit;
import com.sina.sparrowframework.tools.utility.StringToolkit;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;


@Component
@RocketMQTransactionListener(txProducerGroup = Constant.txProducerGroup)
public class SparrowMQTransactionListener implements RocketMQLocalTransactionListener {
    private static final Logger log = LoggerFactory.getLogger(SparrowMQTransactionListener.class);
    @Autowired
    private LocalTransactionService localTransactionService;

    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(final Message msg, final Object arg) {
        return RocketMQLocalTransactionState.UNKNOWN;
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(final Message msg) {

        /**
         * message headers for the message (never {@code null} but may be empty).
         */
        String transactionId = msg.getHeaders().get(RocketMQUtil.toRocketHeaderKey(RocketMQHeaders.TRANSACTION_ID), String.class);
        /**
         * payload (never {@code null}
         *
         */
        byte[] body = (byte[]) msg.getPayload();

        try {

            if (ObjectToolkit.isEmpty(msg)  || StringToolkit.isBlank(transactionId)) {
                log.error("rocketmq checkLocalTransaction msg or transactionId is null, transactionId:{} , body:{} ." ,
                        transactionId ,
                        new String(body)
                );
                return RocketMQLocalTransactionState.ROLLBACK;
            }

        } catch (Exception e) {
            log.error("error info rocketmq checkLocalTransaction convertToRocketMessage transactionId:{} , body:{} ." ,
                    transactionId ,
                    new String(body) ,
                    e
            );
            return RocketMQLocalTransactionState.ROLLBACK;
        }

        try {

            log.info("rocketmq checkLocalTransaction transactionId:{} , body:{} ." , transactionId , new String(body));

            if (localTransactionService.exist(transactionId)) {
                return RocketMQLocalTransactionState.COMMIT;
            }else {
                return RocketMQLocalTransactionState.ROLLBACK;
            }
        } catch (Exception e) {
            log.error("error info rocketmq checkLocalTransaction body:{}." , msg.getPayload() , e);
            return RocketMQLocalTransactionState.UNKNOWN;
        }


    }
}


