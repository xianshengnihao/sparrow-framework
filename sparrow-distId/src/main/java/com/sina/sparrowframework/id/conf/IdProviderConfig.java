package com.sina.sparrowframework.id.conf;

import com.sina.sparrowframework.id.IdProvider;
import com.sina.sparrowframework.id.MachineIdProvider;
import com.sina.sparrowframework.id.Utils;
import com.sina.sparrowframework.id.impl.IdProvidermpl;
import com.sina.sparrowframework.id.impl.IpConfigurableMachineIdProvider;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;


/**
 * Created by wxn on 2018/7/5
 */
@Configuration
public class IdProviderConfig implements EnvironmentAware {

    public static IdProvider id ;

    private Environment environment;

    @Bean
    public MachineIdProvider getMachineIdProvider(){
        String currentSys = environment.getProperty("spring.application.name", String.class);
        String applicationNameGroup= environment.getProperty("application.name.group", String.class);

        String idproviderIps = environment.getProperty("snowflake.id.provider.ips", String.class, Utils.getHostIp());
        IpConfigurableMachineIdProvider provider = new IpConfigurableMachineIdProvider(idproviderIps,currentSys,applicationNameGroup);
        return provider;
    }

    @Bean
    public IdProvider getIdProvider(){
        /**
         * 目前方式可供使用34年
         * genMethod由大变小会导致id比修改之前生成的id小，间接导致数据库索引重做，建议使用默认值
         * version>0时会导致生成的long型数字为负数，不建议修改，推荐使用默认值0
         * idType由大变小会导致id比修改之前生成的id小，间接导致数据库索引重做，建议使用默认值
         */
        IdProvidermpl provider = new IdProvidermpl();
        provider.setMachineIdProvider(getMachineIdProvider());
        provider.init();
        id = provider;
        return provider;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
