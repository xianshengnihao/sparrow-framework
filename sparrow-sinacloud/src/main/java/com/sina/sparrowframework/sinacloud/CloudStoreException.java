package com.sina.sparrowframework.sinacloud;


import com.sina.sparrowframework.tools.struct.ResultCode;
import com.sina.sparrowframework.exception.business.RuntimeBusinessException;

/**
 * created  on 2018/10/12.
 */
public class CloudStoreException extends RuntimeBusinessException {


    private static final long serialVersionUID = 4723549333291204873L;

    public CloudStoreException(ResultCode resultCode) {
        super( resultCode,resultCode.display() );
    }

    public CloudStoreException(ResultCode resultCode, Throwable cause,String format,Object... args) {
        super( resultCode, cause,format,args );
    }
}
