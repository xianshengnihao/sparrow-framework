package com.sina.sparrowframework.log.adaptor;

import com.sina.sparrowframework.log.converter.LogIdPatternConverter;
import com.sina.sparrowframework.tools.utility.JacksonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.sina.sparrowframework.log.adaptor.LogRequestBodyAdvice.getRequest;

/**
 * @author wxn
 * @date 2021/7/10 3:02 下午
 */
@ControllerAdvice
public class LogResponseBodyAdvice implements ResponseBodyAdvice<Object>, EnvironmentAware {
    public Logger logger= LoggerFactory.getLogger(this.getClass());

    private Environment env;

    private static final String SPARROW_LOG_FILTER_RESPONSE_URI_LIST = "sparrow.log.filter.response.uri.list";

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body
            , MethodParameter returnType
            , MediaType selectedContentType
            , Class<? extends HttpMessageConverter<?>> selectedConverterType
            , ServerHttpRequest serverHttpRequest, ServerHttpResponse response) {
        HttpServletRequest request = getRequest();
        Long endMilli = System.currentTimeMillis();
        Long startMilli = LogRequestBodyAdvice.logThreadLocal.get() ==null ? endMilli
                : LogRequestBodyAdvice.logThreadLocal.get();
        String obj  ;
        if (body == null) {
            obj =null;
        }
        else if (body instanceof String) {
            obj = (String) body;
        }else {
            obj = JacksonUtil.objectToJson(body);
        }
        if (!StringUtils.isEmpty(env.getProperty(SPARROW_LOG_FILTER_RESPONSE_URI_LIST,String.class))
                && env.getProperty(SPARROW_LOG_FILTER_RESPONSE_URI_LIST, List.class).contains(request.getRequestURI())) {
            logger.info("请求控制层反馈  URL:{} result:{}", request.getRequestURL(),"响应数据禁止输出");
        }else {
            logger.info("请求控制层反馈 url = {} result{} costsTime = {} mills", request.getRequestURL()
                    ,obj ,endMilli - startMilli
            );
        }
        LogIdPatternConverter.clearLogId();
        return body;
    }

    @Override
    public void setEnvironment(Environment environment) {
        env = environment;
    }
}
