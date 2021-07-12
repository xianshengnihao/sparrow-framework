package com.sina.sparrowframework.log.converter;

import com.sina.sparrowframework.tools.utility.JacksonUtil;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.MethodParameter;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * ResponseBody重置
 *
 * @author tianye6
 * @date 2019/7/10 14:31
 */
@ControllerAdvice
public class LogResponseBodyAdvice implements ResponseBodyAdvice<Object>, EnvironmentAware {

    private Environment env;


    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body
            , MethodParameter returnType
            , MediaType selectedContentType
            , Class<? extends HttpMessageConverter<?>> selectedConverterType
            , ServerHttpRequest request, ServerHttpResponse response) {
        HttpServletRequest oirRequest = LogRequestBodyAdvice.getRequest();

        System.out.println("Url="+oirRequest.getRequestURI());
        System.out.println("body="+ JacksonUtil.objectToJson(body));
        return body;

    }


}
