package com.sina.sparrowframework.template;


import com.sina.sparrowframework.exception.business.BizFailException;
import com.sina.sparrowframework.metadata.constants.CodeManager;

/**
 * 当 {@link TastyTemplateEngine} 出错时抛出.
 * created  on 2018-12-19.
 */
public class TemplateEngineException extends BizFailException {


    private static final long serialVersionUID = -8814687630405389592L;

     TemplateEngineException(CodeManager errorCode) {
        super(errorCode);
    }

     TemplateEngineException(CodeManager errorCode, String message,Object... args) {
        super(errorCode, message,args);
    }


}
