package com.sina.sparrowframwork.exception.business;


import com.sina.sparrowframework.tools.struct.ResultCode;

/**
 * 数据异常,输入参数错误时抛出.
 * created  on 02/03/2018.
 */
public class DataException extends RuntimeBusinessException {


    private static final long serialVersionUID = 6338722095761372507L;

    public DataException(String message, Object... args) {
        super( ResultCode.dataError, message,args );
    }


    public DataException(ResultCode resultCode, String message, Object... args) {
        super( resultCode, message ,args);
    }

    @Deprecated
    public DataException(ResultCode resultCode, String message, Throwable cause) {
        super( resultCode, message, cause );
    }

    public DataException(ResultCode resultCode, Throwable cause) {
        super( resultCode, cause );
    }
}
