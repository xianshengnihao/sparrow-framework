package com.sina.sparrowframework.rocketmq.config;

public class RocketMQConfigUtils {
    /**
     * The bean name of the internally managed RocketMQ transaction annotation processor.
     */
    public static final String ROCKETMQ_TRANSACTION_ANNOTATION_PROCESSOR_BEAN_NAME =
        "com.sina.sparrowframework.rocketmq.config.internalRocketMQTransAnnotationProcessor";

    public static final String ROCKETMQ_TRANSACTION_DEFAULT_GLOBAL_NAME =
        "rocketmq_transaction_default_global_name";

    public static final String ROCKETMQ_TEMPLATE_DEFAULT_GLOBAL_NAME =
            "rocketMQTemplate";
}
