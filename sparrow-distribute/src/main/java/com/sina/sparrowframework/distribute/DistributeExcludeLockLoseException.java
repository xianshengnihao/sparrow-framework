package com.sina.sparrowframework.distribute;


import com.sina.sparrowframework.tools.struct.ResultCode;
import com.sina.sparrowframwork.exception.business.RuntimeBusinessException;

/**
 * 当分布式排他锁丢失时抛出 可选择性抛出.
 * @see DistributeLock
 * created  on 2018/9/14.
 */
public class DistributeExcludeLockLoseException extends RuntimeBusinessException {


    private static final long serialVersionUID = 8288581405711022491L;

    public DistributeExcludeLockLoseException(String message) {
        super( ResultCode.distributeLockLose, message );
    }

    public DistributeExcludeLockLoseException(String message, Throwable cause) {
        super( ResultCode.distributeLockLose,message, cause );
    }

    public DistributeExcludeLockLoseException( Throwable cause) {
        super( ResultCode.distributeLockLose, cause );
    }
}
