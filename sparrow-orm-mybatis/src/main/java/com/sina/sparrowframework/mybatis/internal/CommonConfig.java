package com.sina.sparrowframework.mybatis.internal;

import com.sina.sparrowframework.tools.utility.Assert;
import com.sina.sparrowframework.tools.utility.StrPool;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

/**
 * 基础服务类
 * @date 2019/7/7 10:42
 */
public abstract class CommonConfig implements EnvironmentAware, ApplicationContextAware {

    public ApplicationContext applicationContext;
    public Environment env;

    /**
     * 获取topic名称
     * @param baseTopicName
     * @param tags
     * @return
     */
    public String getTopicName(String baseTopicName, String... tags) {
        Assert.notNull(baseTopicName, "baseTopicName required");
        StringBuilder sb = new StringBuilder();
        sb.append(env.getRequiredProperty(baseTopicName));
        if (ArrayUtils.isNotEmpty(tags)) {
            sb.append(StrPool.COLON);
            if (tags.length == 1) {
                sb.append(tags[0]);
            } else {
                for (int i = 0; i < tags.length; i++) {
                    if (i == tags.length - 1) {
                        sb.append(tags[i]);
                    } else {
                        sb.append(tags[i]).append(StrPool.COLON);
                    }
                }
            }
        }
        return sb.toString();
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
