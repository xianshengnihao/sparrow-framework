package com.sina.sparrowframework.redis.locksimple;


import com.sina.sparrowframework.exception.business.BizFailException;
import com.sina.sparrowframework.metadata.constants.BaseCode;

/**
 * 当分布式排他锁丢失时抛出 可选择性抛出.
 * @see DistributeLock
 * created  on 2018/9/14.
 */
/////////////////////////////////////////////////////////////////////////////
public class DistributeExcludeLockLoseException extends BizFailException {


    private static final long serialVersionUID = 8288581405711022491L;

    public DistributeExcludeLockLoseException(String message) {
        super(BaseCode.ASSERT_ERROR, message );
    }

    public DistributeExcludeLockLoseException(String message, Throwable cause) {
        super( BaseCode.ASSERT_ERROR,message, cause );
    }

    public DistributeExcludeLockLoseException( Throwable cause) {
        super( BaseCode.ASSERT_ERROR, cause );
    }
}
