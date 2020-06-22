package com.sina.sparrowframework.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

/**
 * 所有不包含事务的测试基类
 * created  on 2018/5/8.
 */
public abstract class AbstractTestNGTests extends AbstractTestNGSpringContextTests implements EnvironmentAware {

    protected final Logger LOG = LoggerFactory.getLogger(getClass()  );

    protected Environment env;

    @Override
    public void setEnvironment(Environment environment) {
        env = environment;
    }
}
