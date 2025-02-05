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
    public BizFailException(CodeManager errorCode) {
        super(errorCode.getDesc());
        this.errorCode = errorCode;
    }
    public BizFailException(CodeManager errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BizFailException(CodeManager errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    public BizFailException(CodeManager errorCode, Throwable cause) {
        super(errorCode.getDesc(), cause);
        this.errorCode = errorCode;
    }
    public BizFailException(CodeManager errorCode, String message,Object... args) {
        super(createMessage(message,args));
        this.errorCode = errorCode;
    }
    public BizFailException(CodeManager errorCode, String message,Throwable cause,Object... args) {
        super(createMessage(message,args),cause);
        this.errorCode = errorCode;
    }
    static String createMessage(String format, Object... args) {
        String msg;
        if (format != null && args != null && args.length > 0) {
            msg = String.format( format, args );
        } else {
            msg = format;
        }
        return msg;
    }

}
