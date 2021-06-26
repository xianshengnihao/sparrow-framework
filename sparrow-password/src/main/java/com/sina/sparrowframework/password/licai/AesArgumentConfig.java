package com.sina.sparrowframework.password.licai;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.*;
import org.springframework.http.converter.cbor.MappingJackson2CborHttpMessageConverter;
import org.springframework.http.converter.feed.AtomFeedHttpMessageConverter;
import org.springframework.http.converter.feed.RssChannelHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.JsonbHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.smile.MappingJackson2SmileHttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wxn
 * @date 2021/6/10 10:57 上午
 */
@Configuration
public class AesArgumentConfig  implements WebMvcConfigurer {

    private  List<HttpMessageConverter<?>> aesConverters = new ArrayList<>();

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(this.handlerMethodAesArgument());

    }

    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> handlers) {
        handlers.add(this.handlerMethodAesArgument());
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        aesConverters.addAll(converters);
    }

    @Bean
    public HandlerMethodAesArgument handlerMethodAesArgument(){
        return new HandlerMethodAesArgument(getAesHttpMessageConverter());
    }

    private List<HttpMessageConverter<?>> getAesHttpMessageConverter(){
        aesConverters.add(new ByteArrayHttpMessageConverter());
        return aesConverters;
    }
}
