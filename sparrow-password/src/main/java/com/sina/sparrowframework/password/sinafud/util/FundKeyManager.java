package com.sina.sparrowframework.password.sinafud.util;


import com.sina.sparrowframework.password.sinafud.FundBusinessClient;
import com.sina.sparrowframework.tools.utility.KeyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.security.PrivateKey;
import java.security.PublicKey;


/**
 * @author wxn
 */
@Configuration
@Component("FundPasswordKeyManager")
public class FundKeyManager implements EnvironmentAware  {



    private Environment environment;

    //################################### business key properties #####################################

    public static PublicKey businessPlatformPublicKey;

    public static PrivateKey businessPlatformPrivateKey;

    public static PublicKey businessPartnerPublicKey;

    public static String baseUrl;

    public static String merchantKey;

    @PostConstruct
    private  void initBusiness() throws Exception {

        FundKeyManager.businessPlatformPublicKey =
                KeyUtils.readRsaPublicKey(environment.getProperty("sinafund.business.platformPublicKey"));

        FundKeyManager.businessPlatformPrivateKey
                = KeyUtils.readRsaPrivateKey(environment.getProperty("sinafund.business.platformPrivateKey"));
        FundKeyManager.businessPartnerPublicKey
                = KeyUtils.readRsaPublicKey(environment.getProperty("sinafund.business.partnerPublicKey"));
        FundKeyManager.merchantKey = environment.getProperty("sinafund.business.merchantKey");
        FundKeyManager.baseUrl = environment.getProperty("sinafund.domain.name");
    }

    @Autowired
    public void setRestTemplate(@Qualifier("restPasswordTemplate")RestTemplate restPasswordTemplate) {
        FundBusinessClient.setRestTemplate(restPasswordTemplate);
    }

    @Override
    @Autowired
    public void setEnvironment(Environment environment) {
        this.environment = environment;
        FundBusinessClient.setEnvironment(environment);
    }
}
