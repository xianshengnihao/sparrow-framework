package com.sina.sparrowframework.redis.lock.manager;

/**
 * 有返回值的业务执行接口
 *
 * @author tianye6
 * @date 2019/4/30 15:32
 */
public interface ReturnCallBack<T> {

    /**
     * 执行方法
     *
     * @return
     */
    T execute();
}
