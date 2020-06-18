package com.sina.sparrowframework.sinacloud.config;

import com.sina.cloudstorage.auth.AWSCredentials;
import com.sina.cloudstorage.auth.BasicAWSCredentials;
import com.sina.cloudstorage.services.scs.SCS;
import com.sina.cloudstorage.services.scs.SCSClient;
import com.sina.sparrowframework.sinacloud.CloudStore;
import com.sina.sparrowframework.sinacloud.SinaCloudStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * created  on 2019-05-13.
 */
@ConditionalOnClass(value = {SCS.class})
@ConditionalOnProperty(prefix = "tasty.cloud.store",name = "enable",havingValue = "true",matchIfMissing = true)
@Configuration
public class TastyCloudStoreAuto implements EnvironmentAware {

    private Environment env ;

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }

    @Bean
    public CloudStore tastyCloudStore(){


        String accessKey = env.getRequiredProperty("tasty.sina.cloud.accessKey");
        String secretKey = env.getRequiredProperty("tasty.sina.cloud.secretKey");

        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

        SinaCloudStore sinaCloudStore =  new SinaCloudStore();
        sinaCloudStore.setSinaScs(new SCSClient(credentials));
        return sinaCloudStore;
    }

}
