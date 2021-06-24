package com.sina.sparrowframework.password.conf;

import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.*;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;


/**
 * @author wxn
 */
@Configuration
public class RestTemplatePasswordConfig implements EnvironmentAware {

    protected Environment env;

    @Bean("restPasswordTemplate")
    public RestTemplate restPasswordTemplate(
            @Qualifier("simplePasswordClientHttpRequestFactory")
                    ClientHttpRequestFactory simplePasswordClientHttpRequestFactory) {
        RestTemplate restTemplate = new RestTemplate(simplePasswordClientHttpRequestFactory);
        List<ClientHttpRequestInterceptor> interceptorsTimeout = new ArrayList<>();
        interceptorsTimeout.add(new HeaderRequestInterceptor());
        restTemplate.setInterceptors(interceptorsTimeout);
        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
        restTemplate.getMessageConverters().set(1,stringConverter);
        return restTemplate;
    }

    @Bean("simplePasswordClientHttpRequestFactory")
    public ClientHttpRequestFactory simplePasswordClientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient());
        return factory;
    }
    private HttpClient httpClient() {
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", SSLConnectionSocketFactory.getSocketFactory())
                .build();

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);
        connectionManager.setMaxTotal(env.getProperty("global.http.max.total.num",Integer.class,500));
        connectionManager.setDefaultMaxPerRoute(env.getProperty("global.http.max.after.Route",Integer.class,100));
        connectionManager.setValidateAfterInactivity(env.getProperty("global.http.validate.after.timeout",Integer.class,6000));
        //socket.timeout 服务器返回数据(response)的时间，超过抛出read timeout
        //connection.timeout 连接上服务器(握手成功)的时间，超出抛出connect timeout
        //read.timeout 从连接池中获取连接的超时时间，超时间未拿到可用连接，
        // 会抛出org.apache.http.conn.ConnectionPoolTimeoutException: Timeout waiting for connection from pool

        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(env.getProperty("global.http.client.socket.timeout",Integer.class,10000))
                .setConnectTimeout(env.getProperty("global.http.client.connection.timeout",Integer.class,6000))
                .setConnectionRequestTimeout(env.getProperty("global.http.client.read.timeout",Integer.class,10000))
                .build();
        return HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(connectionManager)
                .setRetryHandler(httpRequestRetryHandler())
                .setSSLContext(trustSelfSignedSSL())
                .setSSLHostnameVerifier(new NoopHostnameVerifier())
                .build();
    }
    @Bean
    public HttpRequestRetryHandler httpRequestRetryHandler(){
       return  new HttpRequestRetryHandler() {
           @Override
           public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
               // 如果已经重试了2次，就放弃
               if (executionCount >= env.getProperty("global.http.client.retry.num", Integer.class, 2)) {
                   return false;
               }
               if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
                   return true;
               }
               if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
                   return false;
               }
               if (exception instanceof InterruptedIOException) {// 超时
                   return false;
               }
               if (exception instanceof UnknownHostException) {// 目标服务器不可达
                   return false;
               }
               if (exception instanceof ConnectTimeoutException) {// 连接被拒绝
                   return false;
               }
               if (exception instanceof SSLException) {// ssl握手异常
                   return false;
               }
               return false;
           }
       };

    }
    private static class HeaderRequestInterceptor implements ClientHttpRequestInterceptor {
        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                            ClientHttpRequestExecution execution) throws IOException {
            HttpRequest wrapper = new HttpRequestWrapper(request);
            wrapper.getHeaders().set("Accept-charset", "utf-8");
            String ContentTypeValue =  wrapper.getHeaders().getFirst("Content-Type");
            if (!StringUtils.isEmpty(ContentTypeValue)
                    && ContentTypeValue.contains(MediaType.APPLICATION_FORM_URLENCODED_VALUE)) {
                wrapper.getHeaders().set("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);
            }
            wrapper.getHeaders().set("charset", "UTF-8");
            return execution.execute(wrapper, body);
        }
    }

    public SSLContext trustSelfSignedSSL()  {
        try {
            return  SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy() {
                        @Override
                        public boolean isTrusted(
                                X509Certificate[] x509Certificates, String s) throws CertificateException {
                            return true;
                        }
                    }).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    @Autowired
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }
}
