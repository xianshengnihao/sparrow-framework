package com.sina.sparrowframework.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;

/**
 * 所有 带事务的测试类的基类
 * created  on 2018/5/8.
 */
public abstract class AbstractTestNGTxTests extends AbstractTransactionalTestNGSpringContextTests implements EnvironmentAware {

    protected final Logger LOG = LoggerFactory.getLogger(getClass()  );

    protected Environment env;

    @Override
    public void setEnvironment(Environment environment) {
        env = environment;
    }

}
