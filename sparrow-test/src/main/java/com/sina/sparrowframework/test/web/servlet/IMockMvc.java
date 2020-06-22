package com.sina.sparrowframework.test.web.servlet;

import com.sina.sparrowframework.test.AbstractTestNGServerTests;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;

/**
 * 这个类用于实现在 server test 时实现 {@link org.springframework.test.web.servlet.MockMvc} 的代理
 * created  on 2018/5/24.
 * @see AbstractTestNGServerTests#getMockMvc()
 */
public interface IMockMvc {

    ResultActions perform(RequestBuilder requestBuilder) throws Exception;

}
