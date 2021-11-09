package com.sina.sparrowframework.password.wjaccount.utils;

import com.sina.sparrowframework.password.wjaccount.WjAccountClient;
import com.sina.sparrowframework.tools.utility.KeyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.security.PublicKey;


/**
 * @author wxn
 */
@Configuration
public class WjAccountKeyManager implements EnvironmentAware  {



    private Environment environment;

    //################################### business key properties #####################################

    public static PublicKey financePartnerPublicKey;

    public static String financePartnerPublicKeyStr;

    public static String appId;

    public static String appKey;

    public static String baseUrl;



    @PostConstruct
    private  void initBusiness() throws Exception {
        WjAccountKeyManager.financePartnerPublicKey =
                KeyUtils.readRsaPublicKey(environment.getProperty("sinaFinance.business.partnerPublicKey"));
        financePartnerPublicKeyStr = environment.getProperty("sinaFinance.business.partnerPublicKey");
        WjAccountKeyManager.appId = environment.getProperty("sinaFinance.business.appId");
        WjAccountKeyManager.appKey = environment.getProperty("sinaFinance.business.appKey");
        WjAccountKeyManager.baseUrl = environment.getProperty("sinaFinance.domain.name");
    }

    @Autowired
    public void setRestTemplate(@Qualifier("restPasswordTemplate")RestTemplate restTemplate) {
        WjAccountClient.setRestTemplate(restTemplate);
    }

    @Override
    @Autowired
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Autowired
    public void setStringRedisTemplate( @Qualifier("stringRedisTemplate") StringRedisTemplate stringRedisTemplate) {
        WjAccountClient.setStringRedisTemplate(stringRedisTemplate);
    }

}
