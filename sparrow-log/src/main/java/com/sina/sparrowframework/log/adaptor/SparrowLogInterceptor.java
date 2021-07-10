package com.sina.sparrowframework.log.adaptor;

import com.sina.sparrowframework.log.converter.LogIdPatternConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.NamedThreadLocal;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author wxn
 * @date 2021/7/10 3:02 下午
 */
public class SparrowLogInterceptor extends HandlerInterceptorAdapter implements EnvironmentAware, InitializingBean {

    public Logger logger= LoggerFactory.getLogger(this.getClass());
    private final NamedThreadLocal<Long> logThreadLocal = new NamedThreadLocal<>("接口耗时计算");

    private Environment env;
    /**
     * h5端请求日志过滤
     */
    private static final String SPARROW_LOG_FILTER_REQUEST_URI_LIST = "sparrow.log.filter.request.uri.list";
    private static final String SPARROW_LOG_FILTER_RESPONSE_URI_LIST = "sparrow.log.filter.response.uri.list";
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        LogHttpServletRequest logHttpServletRequest = new LogHttpServletRequest(request);
        LogHttpServletResponse logHttpServletResponse = new LogHttpServletResponse(response);
        String requestBody = logHttpServletRequest.getRequestBody();
        logThreadLocal.set(System.currentTimeMillis());
        if (!StringUtils.isEmpty(env.getProperty(SPARROW_LOG_FILTER_REQUEST_URI_LIST,String.class))
                && env.getProperty(SPARROW_LOG_FILTER_REQUEST_URI_LIST, List.class).contains(request.getRequestURI())) {
            logger.info("请求控制层参数  URL:{} request body:{}", logHttpServletRequest.getRequestURL(),"请求数据禁止输出");

        }else {
            logger.info("请求控制层参数  URL:{} request body:{}", logHttpServletRequest.getRequestURL(),requestBody);
        }

        return super.preHandle(logHttpServletRequest, logHttpServletResponse, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response
            , Object handler, Exception ex) throws Exception {
        if (response instanceof LogHttpServletResponse) {
            LogHttpServletResponse logHttpServletResponse = (LogHttpServletResponse) response;
            Long startMilli = logThreadLocal.get();
            Long endMilli = System.currentTimeMillis();
            if (!StringUtils.isEmpty(env.getProperty(SPARROW_LOG_FILTER_RESPONSE_URI_LIST,String.class))
                    && env.getProperty(SPARROW_LOG_FILTER_RESPONSE_URI_LIST, List.class).contains(request.getRequestURI())) {
                logger.info("请求控制层反馈  URL:{} result:{}", request.getRequestURL(),"响应数据禁止输出");
            }else {
                logger.info("请求控制层反馈 url = {} result{} costsTime = {} mills", request.getRequestURL()
                        , logHttpServletResponse.getBodyString(),endMilli - startMilli
                );
                logHttpServletResponse.copyToResponse();
            }
        }else {
            logger.info("请求控制层反馈 url = {} result{}", request.getRequestURL(), "LogHttpServletResponse 被适配无法输出数据");

        }
        LogIdPatternConverter.clearLogId();
        super.afterCompletion(request, response, handler, ex);
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }
}
