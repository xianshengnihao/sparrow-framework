package com.sina.sparrowframework.rocketmq.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface ExtRocketMQTemplateConfiguration {
    /**
     * The component name of the Producer configuration.
     */
    String value() default "";

    /**
     * The property of "name-server".
     */
    String nameServer();

    /**
     * Name of producer.
     */
    String group() default "${rocketmq.producer.group:}";
    /**
     * Millis of send message timeout.
     */
    int sendMessageTimeout() default -1;
    /**
     * Compress message body threshold, namely, message body larger than 4k will be compressed on default.
     */
    int compressMessageBodyThreshold() default -1;
    /**
     * Maximum number of retry to perform internally before claiming sending failure in synchronous mode.
     * This may potentially cause message duplication which is up to application developers to resolve.
     */
    int retryTimesWhenSendFailed() default -1;
    /**
     * <p> Maximum number of retry to perform internally before claiming sending failure in asynchronous mode. </p>
     * This may potentially cause message duplication which is up to application developers to resolve.
     */
    int retryTimesWhenSendAsyncFailed() default -1;
    /**
     * Indicate whether to retry another broker on sending failure internally.
     */
    boolean retryNextServer() default false;
    /**
     * Maximum allowed message size in bytes.
     */
    int maxMessageSize() default -1;
    /**
     * The property of "access-key".
     */
    String accessKey() default "${rocketmq.producer.accessKey:}";
    /**
     * The property of "secret-key".
     */
    String secretKey() default "${rocketmq.producer.secretKey:}";
    /**
     * Switch flag instance for message trace.
     */
    boolean enableMsgTrace() default true;
    /**
     * The name value of message trace topic.If you don't config,you can use the default trace topic name.
     */
    String customizedTraceTopic() default "${rocketmq.producer.customized-trace-topic:}";
}