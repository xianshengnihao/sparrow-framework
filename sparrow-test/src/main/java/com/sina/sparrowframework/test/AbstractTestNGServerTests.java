package com.sina.sparrowframework.test;

import com.sina.sparrowframework.test.web.servlet.IMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.servlet.Filter;
import java.nio.charset.StandardCharsets;

/**
 * 所有服务端测试的基类
 * created  on 2018/5/8.
 * @see IMockMvc
 */
public abstract class AbstractTestNGServerTests extends AbstractTestNGTests {


    @Autowired
    protected WebApplicationContext wac;


    protected IMockMvc getMockMvc() {
        DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup( this.wac );
        if (registerCharacterEncodingFilter()) {
            doRegisterCharacterEncodingFilter( builder );
        }
        Filter[] filters = getServletFilters();
        if (!ObjectUtils.isEmpty( filters )) {
            builder.addFilters( filters );
        }

        return createMockMvc( builder.build() );
    }


    protected IMockMvc createMockMvc(MockMvc mockMvc){
        return mockMvc::perform;
    }


    private void doRegisterCharacterEncodingFilter(DefaultMockMvcBuilder builder) {
        CharacterEncodingFilter filter;

        filter = new CharacterEncodingFilter();
        filter.setEncoding( StandardCharsets.UTF_8.name() );
        filter.setForceEncoding( true );
        filter.setBeanName( "characterEncodingFilter" );

        builder.addFilter( filter, "/*" );
    }

    protected boolean registerCharacterEncodingFilter() {
        return true;
    }

    protected Filter[] getServletFilters() {
        return null;
    }



}
