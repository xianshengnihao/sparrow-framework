package com.sina.sparrowframework.redis.lock.manager;

import com.sina.sparrowframework.redis.lock.IDistributedLock;
import com.sina.sparrowframework.redis.lock.LockProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
/**
 *
 * @author tianye6
 * @date 2019/4/30 15:37
 */
@Component
public class LuaRedisLockManager implements ILockManager, EnvironmentAware {

    private final Logger logger = LoggerFactory.getLogger(LuaRedisLockManager.class);

    @Autowired
    private IDistributedLock distributeLock;
    private Environment environment;

    @Override
    public void callBack(String lockKey, LockCallBack callBack) {
        Assert.notNull(lockKey, "lockKey can't not be null");
        Assert.notNull(callBack, "callBack can't not be null");
        try {
            //获取锁
            distributeLock.lock(lockKey, getLockProperties());
            //执行业务
            callBack.execute();
        } catch (Exception e) {
            logger.error("redis加锁同步执行业务发生异常：{}", e.getMessage(), e);
        } finally {
            distributeLock.unLock(lockKey);
        }

    }

    @Override
    public <T> T callBack(String lockKey, ReturnCallBack<T> callBack) {
        Assert.notNull(lockKey, "lockKey can't not be null");
        Assert.notNull(callBack, "callBack can't not be null");
        T t = null;
        try {
            //获取锁
            distributeLock.lock(lockKey, getLockProperties());
            //执行业务
            t = callBack.execute();
        } catch (Exception e) {
            logger.error("redis加锁同步执行业务发生异常：{}", e.getMessage(), e);
        } finally {
            //释放锁
            distributeLock.unLock(lockKey);
        }
        return t;
    }

    private LockProperties getLockProperties() {
        return new LockProperties();
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
