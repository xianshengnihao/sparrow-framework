package com.sina.sparrowframework.password.wjmiddle.util;


import com.sina.sparrowframework.password.wjmiddle.WjMiddleServiceClient;
import com.sina.sparrowframework.tools.utility.KeyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.security.PrivateKey;


/**
 * @author wxn
 */
@Configuration
public class MiddleKeyManager implements EnvironmentAware  {

    private Environment environment;

    //################################### business key properties #####################################
    public static String business;
    public static PrivateKey businessPartnerPrivateKey;
    public static String baseUrl;

    @PostConstruct
    private  void initBusiness() throws Exception {
        MiddleKeyManager.businessPartnerPrivateKey =
                KeyUtils.readRSAPrivateKey(environment.getProperty("wj.middle.business.partnerPrivateKey"));
        MiddleKeyManager.baseUrl = environment.getProperty("wj.middle.domain.name");
        MiddleKeyManager.business = environment.getProperty("wj.middle.business.code");

    }

    @Autowired
    public void setRestTemplate(@Qualifier("restPasswordTemplate")RestTemplate restPasswordTemplate) {
        WjMiddleServiceClient.setRestTemplate(restPasswordTemplate);
    }
    @Override
    @Autowired
    public void setEnvironment(Environment environment) {
        this.environment = environment;
        WjMiddleServiceClient.setEnvironment(environment);
    }
}
