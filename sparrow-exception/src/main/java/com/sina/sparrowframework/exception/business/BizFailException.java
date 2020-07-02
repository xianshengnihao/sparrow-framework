package com.sina.sparrowframework.exception.business;

import com.sina.sparrowframework.metadata.constants.CodeManager;

/**
 * Created by wxn on 2019-06-30
 */
@SuppressWarnings("unused")
public class BizFailException extends RuntimeException {

    private final transient CodeManager errorCode;

    public CodeManager getErrorCode() {
        return this.errorCode;
    }

    public BizFailException(CodeManager errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BizFailException(CodeManager errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

}
