package com.sina.sparrowframework.exception.business;

/**
 * Created by wxn on 2019-06-30
 */
public class BizFailException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private String errorCode;

    public String getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public BizFailException() {
    }

    public BizFailException(String message) {
        super(message);
    }

    public BizFailException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
