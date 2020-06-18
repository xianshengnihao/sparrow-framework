package com.sina.sparrowframwork.exception.business;

/**
 * Created by wxn on 2019-06-30
 * 全局捕获异常，忽略日志打印
 */
public class BizFailIgnoreLogException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private String errorCode;

    public String getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public BizFailIgnoreLogException() {
    }

    public BizFailIgnoreLogException(String message) {
        super(message);
    }

    public BizFailIgnoreLogException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
