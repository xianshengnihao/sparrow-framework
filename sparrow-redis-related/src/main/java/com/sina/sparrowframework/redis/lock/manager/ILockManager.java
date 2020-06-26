package com.sina.sparrowframework.redis.lock.manager;

/**
 * 分布式锁统一管理操作接口
 * @date 2019/4/30 15:14
 */
public interface ILockManager {


    /**
     * 加锁并执行业务
     *
     * @param lockKey
     * @param callBack
     */
    void callBack(String lockKey, LockCallBack callBack);

    /**
     * 加锁并返回执行业务的返回值
     *
     * @param lockKey
     * @param callBack
     * @param <T>
     * @return
     */
    <T> T callBack(String lockKey, ReturnCallBack<T> callBack);
}
