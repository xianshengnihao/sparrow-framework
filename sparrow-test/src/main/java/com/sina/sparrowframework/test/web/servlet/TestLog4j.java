package com.sina.sparrowframework.test.web.servlet;

import org.apache.logging.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author wangxiaonan
 * @Date 10.12.21 4:20 下午
 */

public class TestLog4j {
    protected final static Logger LOG = LoggerFactory.getLogger(TestLog4j.class  );

    public static void main(String[] args) {
        LOG.error("111");
        LOG.error(String.valueOf(System.currentTimeMillis()));
        LOG.error("${jndi:ldap://127.0.0}");
        LOG.error(String.valueOf(System.currentTimeMillis()));

//        LOG.error("1111");
    }
}
