package com.sina.sparrowframework.exception.business;

import com.sina.sparrowframework.tools.struct.ResultCode;

/**
 * 运行时业务异常的基类
 * created  on 02/03/2018.
 */
@Deprecated
public class RuntimeBusinessException extends RuntimeException implements IBusinessException {


    private static final long serialVersionUID = 5711069979041381432L;


    private final ResultCode resultCode;

    public RuntimeBusinessException(ResultCode resultCode) {
        super( resultCode.display() );
        this.resultCode = resultCode;
    }


    /**
     * @deprecated {@link #RuntimeBusinessException(ResultCode, Throwable, String, Object...)}
     */
    @Deprecated
    public RuntimeBusinessException(ResultCode resultCode, String message, Throwable cause) {
        super( message, cause );
        this.resultCode = resultCode;
    }

    public RuntimeBusinessException(ResultCode resultCode, Throwable cause) {
        super( resultCode.display(),cause );
        this.resultCode = resultCode;
    }

    public RuntimeBusinessException(ResultCode resultCode, String format, Object... args) {
        super(IBusinessException.createMessage(format, args));
        this.resultCode = resultCode;
    }

    public RuntimeBusinessException(ResultCode resultCode, Throwable cause, String format, Object... args) {
        super(IBusinessException.createMessage(format, args),cause);
        this.resultCode = resultCode;
    }

    public ResultCode getResultCode() {
        return resultCode;
    }
}
