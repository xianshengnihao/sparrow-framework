package com.sina.sparrowframework.rocketmq.core;

import com.sina.sparrowframework.tools.tuple.Triple;
import com.sina.sparrowframework.tools.utility.CollToolkit;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.SendResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.NamedThreadLocal;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>消息的发送其实是事务期间写入到了副本变量中</p>
 * <p>事务提交后</p>
 */
@Component
public class CurrentSendMqHolder {

    private static final Logger logger = LoggerFactory.getLogger(CurrentSendMqHolder.class);
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    private static ThreadLocal<List<Triple<Boolean , SendResult, LocalTransactionState>>> mqMessage = new NamedThreadLocal("MQ消息");

    private static ThreadLocal<Boolean> transactionStart = new NamedThreadLocal("事务开启标识");


    public static Boolean get() { return transactionStart.get(); }


    public static void set(Boolean mid) { transactionStart.set(mid); }


    public static void removeTransactionStart() { transactionStart.remove(); }

    public static List<Triple<Boolean , SendResult, LocalTransactionState>> getTriples() {
        return mqMessage.get();
    }


    public static void setTriples(Triple<Boolean , SendResult, LocalTransactionState> triple) {
        List<Triple<Boolean, SendResult, LocalTransactionState>> list = getTriples();
        if (!CollToolkit.isEmpty(list)) {
            list.add(triple);
            mqMessage.set(list);
        }else {
            List<Triple<Boolean, SendResult, LocalTransactionState>> newList = new ArrayList<>();
            newList.add(triple);
            mqMessage.set(newList);
        }
    }


    public static void removeMqMessage() { mqMessage.remove(); }


    public static void removeAllInfo() {
        mqMessage.remove();
        transactionStart.remove();
    }



    void sendFinalTransatcionMessage( boolean isSuccessed){
        Boolean isOpenTransaction = CurrentSendMqHolder.get();

        if (isOpenTransaction ==null || !isOpenTransaction) {
            CurrentSendMqHolder.removeTransactionStart();
            return;
        }
        List<Triple<Boolean, SendResult, LocalTransactionState>> triples = CurrentSendMqHolder.getTriples();
        if (!CollToolkit.isEmpty(triples) ) {
            for (Triple<Boolean, SendResult, LocalTransactionState> triple : triples) {
                if (triple.getLeft()) {
                    try {
                        LocalTransactionState localTransactionState = triple.getRight();
                        if (!isSuccessed) {
                            localTransactionState = LocalTransactionState.ROLLBACK_MESSAGE;
                        }else {
                            localTransactionState = LocalTransactionState.COMMIT_MESSAGE;
                        }
                        rocketMQTemplate.endTransaction(triple.getMiddle(), localTransactionState);
                    } catch (Exception e) {
                        logger.error("CurrentSendMqHolder  sendFinalTransatcionMessage error:{}." , e.getMessage());
                    }
                }
            }
        }
        CurrentSendMqHolder.removeAllInfo();
    }


}
