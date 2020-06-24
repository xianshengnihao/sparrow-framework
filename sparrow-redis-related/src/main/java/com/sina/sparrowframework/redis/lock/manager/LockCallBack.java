package com.sina.sparrowframework.redis.lock.manager;

/**
 * 无返回值的业务执行接口
 *
 * @author tianye6
 * @date 2019/4/30 15:15
 */
public interface LockCallBack {

    /**
     * 执行方法
     */
    void execute();
}
