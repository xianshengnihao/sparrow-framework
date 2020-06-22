package com.sina.sparrowframework.test;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

/**
 * created  on 2018/5/8.
 */
public interface TestHttpHeaders {

    String CONTENT_TYPE = HttpHeaders.CONTENT_TYPE.concat( "=" ).concat( MediaType.APPLICATION_JSON_UTF8_VALUE );


}
