package com.sina.sparrowframework.sinacloud;


import com.sina.sparrowframework.exception.business.BizFailException;
import com.sina.sparrowframework.metadata.constants.CodeManager;


/**
 * created  on 2018/10/12.
 */
public class CloudStoreException extends BizFailException {


    private static final long serialVersionUID = 4723549333291204873L;

    public CloudStoreException(CodeManager errorCode) {
        super( errorCode,errorCode.getDesc() );
    }

    public CloudStoreException(CodeManager errorCode, Throwable cause,Object... args) {
        super( errorCode, errorCode.getDesc(),cause,args );
    }
}
