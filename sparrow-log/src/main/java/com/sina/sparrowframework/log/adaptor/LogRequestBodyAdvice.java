package com.sina.sparrowframework.log.adaptor;

import com.sina.sparrowframework.log.converter.LogIdPatternConverter;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.MethodParameter;
import org.springframework.core.NamedThreadLocal;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;


/**
 * @author wxn
 * @date 2021/7/10 3:02 下午
 */
@ControllerAdvice
public class LogRequestBodyAdvice extends RequestBodyAdviceAdapter implements EnvironmentAware {
    public Logger logger= LoggerFactory.getLogger(this.getClass());
    public static final NamedThreadLocal<Long> LOG_THREAD_LOCAL = new NamedThreadLocal<>("接口耗时计算");

    private Environment env;
    /**
     * h5端请求日志过滤
     */
    private static final String SPARROW_LOG_FILTER_REQUEST_URI_LIST = "sparrow.log.filter.request.uri.list";

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
            return true;
    }

    /**
     * 具体前置操作
     *
     * @param inputMessage
     * @param parameter
     * @param targetType
     * @param converterType
     * @return
     * @throws IOException
     */
    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
                                           Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        LOG_THREAD_LOCAL.set(System.currentTimeMillis());
        String json = IOUtils.toString(inputMessage.getBody(), StandardCharsets.UTF_8);
        HttpServletRequest request = getRequest();
        String logId = request.getHeader(LogIdPatternConverter.SPARROW_LOG_ID);
        logger.info("请求控制层参数 logId={}",logId);
        if (!StringUtils.isEmpty(logId)) {
            LogIdPatternConverter.putThreadLogId(logId);
        }
        String reqUrl = request.getRequestURI();
        if (!StringUtils.isEmpty(env.getProperty(SPARROW_LOG_FILTER_REQUEST_URI_LIST,String.class))
                && env.getProperty(SPARROW_LOG_FILTER_REQUEST_URI_LIST, List.class).contains(request.getRequestURI())) {
            logger.info("请求控制层参数  URL:{} request body:{}", reqUrl,"请求数据禁止输出");

        }else {
            logger.info("请求控制层参数  URL:{} request body:{}",reqUrl,json);
        }

        final InputStream returnInputStream = IOUtils.toInputStream(json, StandardCharsets.UTF_8);
        final HttpHeaders returnHeaders = inputMessage.getHeaders();
        return new HttpInputMessage() {
            @Override
            public InputStream getBody() throws IOException {
                return returnInputStream;
            }

            @Override
            public HttpHeaders getHeaders() {
                return returnHeaders;
            }
        };
    }

    public static HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request =
                (HttpServletRequest)
                        requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        return request;
    }

    @Override
    public void setEnvironment(Environment environment) {
        env = environment;
    }

}
