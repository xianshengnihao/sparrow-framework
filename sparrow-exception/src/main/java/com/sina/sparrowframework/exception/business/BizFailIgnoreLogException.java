package com.sina.sparrowframework.exception.business;

import com.sina.sparrowframework.metadata.constants.CodeManager;

/**
 * Created by wxn on 2019-06-30
 * 全局捕获异常，忽略日志打印
 */
public class BizFailIgnoreLogException extends RuntimeException {

    private final transient CodeManager errorCode;

    public CodeManager getErrorCode() {
        return this.errorCode;
    }

    public BizFailIgnoreLogException(CodeManager errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BizFailIgnoreLogException(CodeManager errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

}
