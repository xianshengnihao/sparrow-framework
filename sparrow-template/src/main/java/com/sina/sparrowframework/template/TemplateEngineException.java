package com.sina.sparrowframework.template;


import com.sina.sparrowframework.tools.struct.ResultCode;
import com.sina.sparrowframework.exception.business.RuntimeBusinessException;

/**
 * 当 {@link TastyTemplateEngine} 出错时抛出.
 * created  on 2018-12-19.
 */
public class TemplateEngineException extends RuntimeBusinessException {


    private static final long serialVersionUID = -8814687630405389592L;

     TemplateEngineException(ResultCode resultCode) {
        super(resultCode);
    }

     TemplateEngineException(ResultCode resultCode, String message,Object... args) {
        super(resultCode, message,args);
    }


}
