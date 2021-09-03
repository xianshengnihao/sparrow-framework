package com.sina.sparrowframework.log.interceptor;

import com.sina.sparrowframework.log.converter.LogIdPatternConverter;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wxn
 * @date 2021/8/18 11:21 上午
 */
public class FeignLogRequestInterceptor implements RequestInterceptor {

    public Logger logger= LoggerFactory.getLogger(this.getClass());



    @Override
    public void apply(RequestTemplate template) {
        String logId = LogIdPatternConverter.getThreadLogId(Thread.currentThread().getId());
        template.header(LogIdPatternConverter.SPARROW_LOG_ID,logId);
    }
}
