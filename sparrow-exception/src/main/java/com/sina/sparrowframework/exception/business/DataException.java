package com.sina.sparrowframework.exception.business;


import com.sina.sparrowframework.metadata.constants.BaseCode;
import com.sina.sparrowframework.metadata.constants.CodeManager;

/**
 * 数据异常,输入参数错误时抛出.
 */
public class DataException extends BizFailException {


    private static final long serialVersionUID = 6338722095761372507L;

    public DataException(String message, Object... args) {
        super(BaseCode.UNKNOWN_ERROR, message,args );
    }


    public DataException(CodeManager codeManager, String message, Object... args) {
        super( codeManager, message ,args);
    }

    @Deprecated
    public DataException(CodeManager codeManager, String message, Throwable cause) {
        super( codeManager, message, cause );
    }

    public DataException(CodeManager codeManager, Throwable cause) {
        super( codeManager, cause );
    }
}
