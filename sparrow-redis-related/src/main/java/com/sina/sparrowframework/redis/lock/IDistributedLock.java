package com.sina.sparrowframework.redis.lock;

/**
 * 加锁、释放锁操作接口
 *
 * @author tianye6
 * @date 2019/4/29 15:47
 */
public interface IDistributedLock {

    /**
     * 加锁
     *
     * @param key
     * @param lockProperties
     * @return
     */
    boolean lock(String key, LockProperties lockProperties);

    /**
     * 释放锁
     *
     * @param key
     * @return
     */
    boolean unLock(String key);

}
