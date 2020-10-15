package com.sina.sparrowframework.kafka;

import com.sina.sparrowframework.tools.utility.DateUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.time.LocalDateTime;

/**
 * created  on 2019-05-07.
 */
public abstract class KafkaUtils {

    public static String convertString(ConsumerRecord<String, String> record) {
        LocalDateTime time = DateUtil.toDateTime(record.timestamp());

        return "ConsumerRecord(topic = " + record.topic() + ", partition = "
                + record.partition() + ", offset = " + record.offset()
                + ", timestamp = " + time.format(DateUtil.DATETIME_FORMATTER)
                + ", headers = " + record.headers()
                + ", key =  " + record.key() + ", value = " + record.value() + ")";
    }

}
