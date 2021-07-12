package com.sina.sparrowframework.log.converter;

import org.apache.commons.io.IOUtils;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.MethodParameter;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;



@ControllerAdvice
public class LogRequestBodyAdvice extends RequestBodyAdviceAdapter implements EnvironmentAware {
    private Environment env;

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
        String json = IOUtils.toString(inputMessage.getBody(), StandardCharsets.UTF_8);
        HttpServletRequest request = getRequest();
        String reqUri = request.getRequestURI();
        System.out.println("reqUri="+reqUri);
        System.out.println("json="+json);
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
