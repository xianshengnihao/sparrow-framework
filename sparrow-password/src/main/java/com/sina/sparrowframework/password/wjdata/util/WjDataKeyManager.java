package com.sina.sparrowframework.password.wjdata.util;

import com.sina.sparrowframework.password.wjdata.ABTestServiceClient;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Configuration
@Component("WjDataKeyManager")
public class WjDataKeyManager implements EnvironmentAware  {


    private Environment environment;

    //################################### business key properties #####################################

    public static String baseUrl;

    @PostConstruct
    private  void initBusiness() {
        WjDataKeyManager.baseUrl = environment.getProperty("wjData.domain.name");
    }

    @Resource
    public void setRestTemplate(RestTemplate restPasswordTemplate) {
        ABTestServiceClient.setRestTemplate(restPasswordTemplate);
    }

    @Override
    @Resource
    public void setEnvironment(Environment environment) {
        this.environment = environment;
        ABTestServiceClient.setEnvironment(environment);
    }
}
