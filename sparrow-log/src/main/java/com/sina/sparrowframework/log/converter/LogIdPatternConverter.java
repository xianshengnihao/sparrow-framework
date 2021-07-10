package com.sina.sparrowframework.log.converter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.core.pattern.PatternConverter;

import com.sina.sparrowframework.tools.utility.ObjectToolkit;


/**
 * Date:2020/3/20
 * Time:11:45
 * @author wxn
 */
@Plugin(name = "LogIdPatternConverter", category = PatternConverter.CATEGORY)
@ConverterKeys({ "y", "logId" })
public class LogIdPatternConverter extends LogEventPatternConverter {
    private static ThreadLocal<Map<Long,String>> threadIdLocal = new ThreadLocal();

    public static String getThreadLogId(Long threadId) {
        Map<Long,String> threadMap = threadIdLocal.get();
        if (ObjectToolkit.isEmpty(threadMap)) {
            threadMap = new HashMap<>(2);
            threadMap.put(threadId, UUID.randomUUID().toString().replace("-",""));
            threadIdLocal.set(threadMap);
        }
        return threadMap.get(threadId);
    }
    public static void putThreadLogId(Long threadId, String logId) {
        Map<Long, String> threadMap = threadIdLocal.get();
        if (threadMap == null) {
            threadMap = new HashMap<>();
            threadIdLocal.set(threadMap);
        }
        threadIdLocal.get().put(threadId, logId);
    }

    public static void putThreadLogId(String logId) {
        putThreadLogId(Thread.currentThread().getId(), logId);
    }

    public static void clearLogId() {
        threadIdLocal.remove();
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
