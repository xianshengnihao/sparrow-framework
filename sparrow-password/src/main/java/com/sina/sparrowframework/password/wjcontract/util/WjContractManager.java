package com.sina.sparrowframework.password.wjcontract.util;

import com.sina.sparrowframework.password.wjcontract.WjContractClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

/**
 * 微聚合同
 *
 * @Author liuyi
 * @Description
 * @Date 2021/11/8 14:20
 **/
@Component
public class WjContractManager implements EnvironmentAware {

    private static Environment environment;

    public static String appid;
    public static String appSecret;
    public static String baseUrl;

    @PostConstruct
    private void initBusiness() throws Exception {
        appid = environment.getProperty("wjContract.business.appid");
        appSecret = environment.getProperty("wjContract.business.appSecret");
        baseUrl = environment.getProperty("wjContract.domain.name");
    }

    @Autowired
    public void setRestTemplate(@Qualifier("restPasswordTemplate") RestTemplate restPasswordTemplate) {
        WjContractClient.setRestTemplate(restPasswordTemplate);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

}
