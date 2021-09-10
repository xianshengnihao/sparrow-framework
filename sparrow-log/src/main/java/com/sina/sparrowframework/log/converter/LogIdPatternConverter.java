package com.sina.sparrowframework.log.converter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.core.pattern.PatternConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Date:2020/3/20
 * Time:11:45
 * @author wxn
 */
@Plugin(name = "LogIdPatternConverter", category = PatternConverter.CATEGORY)
@ConverterKeys({ "y", "logId" })
public class LogIdPatternConverter extends LogEventPatternConverter {
    public static Logger logger= LoggerFactory.getLogger(LogIdPatternConverter.class);

    public static final String SPARROW_LOG_ID = "sparrow-log-id";
    private static ThreadLocal<Map<Long,String>> logThreadIdLocal = new TransmittableThreadLocal() {
        @Override
        protected Map<Long,String> initialValue() {
            return new ConcurrentHashMap<>(3);
        }
    };
    private static HashMap<Long,String> mainLog =new HashMap();

        public static String getThreadLogId(Long threadId) {
            try {
                if (threadId.equals(1L)) {
                    return Optional.ofNullable(mainLog.get(threadId)).orElseGet(()->{
                        mainLog.put(threadId
                                , UUID.randomUUID().toString().replace("-", ""));
                        return mainLog.get(threadId);
                    });
                }
                Optional<Map.Entry<Long, String>> first = logThreadIdLocal.get()
                        .entrySet().stream().findFirst();
                return first.isPresent() ? first.get().getValue():(String) Optional.empty().orElseGet(()->{
                    logThreadIdLocal.get().put(threadId
                            , UUID.randomUUID().toString().replace("-", ""));
                    return logThreadIdLocal.get().get(threadId);
                });
            } catch (Exception e) {
                logger.info("getThreadLogId 异常",e);
            }
            return null;
        }
    public static void putThreadLogId(Long threadId, String logId) {
        logThreadIdLocal.get().put(threadId, logId);
    }

    public static void putThreadLogId(String logId) {
        putThreadLogId(Thread.currentThread().getId(), logId);
    }
    public static void clearLogId() {
        logThreadIdLocal.remove();
    }

    private static final LogIdPatternConverter INSTANCE =
            new LogIdPatternConverter();

    public static LogIdPatternConverter newInstance(
            final String[] options) {
        return INSTANCE;
    }

    private LogIdPatternConverter(){
        super("LogId", "logId");
    }

    @Override
    public void format(LogEvent logEvent, StringBuilder stringBuilder) {
        String logId = getThreadLogId(logEvent.getThreadId());
        stringBuilder.append(logId);
    }

}
