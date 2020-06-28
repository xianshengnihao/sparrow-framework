package com.sina.sparrowframework.redis.lock;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * redis provider
 */
public class RedisDistLockProvider implements IDistributedLock {

    private static final Logger logger = LoggerFactory.getLogger(RedisDistLockProvider.class);

    private static final String KEY_PREFIX = "sync-lock";
    private static final String ENV_DEFAULT = "default";
    private final RedisScript<Long> LOCK_SCRIPT;
    private final RedisScript<Long> RELEASE_LOCK_SCRIPT;
    private final RedisTemplate redisTemplate;
    private final String environment;
    private static final ThreadLocal<String> LOCAL_REQUEST_IDS = new ThreadLocal<>();

    public RedisDistLockProvider(RedisTemplate redisTemplate) {
        this(redisTemplate, ENV_DEFAULT);
    }

    public RedisDistLockProvider(RedisTemplate redisTemplate, String environment) {
        this.redisTemplate = redisTemplate;
        this.environment = environment;
        this.LOCK_SCRIPT = getLockScript("redis/lua/lock.lua");
        logger.debug("init lua lock script success:\n:{}", LOCK_SCRIPT.getScriptAsString());
        this.RELEASE_LOCK_SCRIPT = getLockScript("redis/lua/releaseLock.lua");
        logger.debug("init lua release lock  script susscess:\n:{}", RELEASE_LOCK_SCRIPT.getScriptAsString());
    }

    private RedisScript<Long> getLockScript(String lockScriptPath) {
        DefaultRedisScript lockScript = new DefaultRedisScript<Long>();
        lockScript.setScriptSource(new ResourceScriptSource(new ClassPathResource(lockScriptPath)));
        lockScript.setResultType(Long.class);
        return lockScript;
    }

    @Override
    public boolean lock(String key, LockProperties lockProperties) {
        Assert.notNull(key, "lock can't be null");
        try {
            boolean result = tryToSetExpiration(key, lockProperties);
            if (result) {
                logger.debug("acquire lock:{} success", key);
                return Boolean.TRUE;
            }
            //获取锁次数
            int lockTryCount = 0;
            int retryCnt = lockProperties.getRetryCnt();
            while (retryCnt-- > 0) {
                //重试获取锁
                logger.debug("thread name :{} ,retry to acquire lock:{}, operate:{} times", Thread.currentThread().getName(), key, lockTryCount);
                try {
                    Thread.sleep(lockProperties.getSleepTime());
                    result = tryToSetExpiration(key, lockProperties);
                    if (result) {
                        logger.debug("retry to acquire lock:{} success, operate:{} times", key, lockTryCount + 1);
                        return Boolean.TRUE;
                    }
                } catch (Exception e) {
                    logger.error("thread name :{} ,acquire redis occured an exception:{}", Thread.currentThread().getName(), e.getMessage(), e);
                    break;
                }
                ++lockTryCount;
            }
        } catch (Exception le) {
            logger.error("thread name :{} ,acquire redis occured an exception:{}", Thread.currentThread().getName(), le.getMessage(), le);
        }
        return false;
    }

    private boolean tryToSetExpiration(String key, LockProperties properties) {
        String redisKey = buildKey(key, this.environment);
        Expiration expiration = getExpiration(properties.getExpiredTime());
        String redisVal = buildValue();
        List<String> keys = Arrays.asList(redisKey, redisVal, String.valueOf(expiration.getExpirationTimeInMilliseconds()));
        Long result = (Long) redisTemplate.execute(LOCK_SCRIPT, keys);
        if (!ObjectUtils.isEmpty(result) && result > 0) {
            LOCAL_REQUEST_IDS.set(redisVal);
            logger.info("thread name :{} ,success to acquire lock:{}, Status code reply:{}", Thread.currentThread().getName(), key, result);
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    private static Expiration getExpiration(Long expirationTime) {
        return Expiration.from(expirationTime, TimeUnit.MILLISECONDS);
    }

    private static String buildKey(String key, String env) {
        return String.format("%s:%s:%s", KEY_PREFIX, env, key);
    }

    private static String buildValue() {
        try {
            return String.format("%s@%s", UUID.randomUUID().toString().replace("-", ""), InetAddress.getLocalHost());
        } catch (UnknownHostException e) {
            logger.error("init threadLocal occured an exception:{}", e.getMessage(), e);
            return UUID.randomUUID().toString().replace("-", "");
        }
    }

    @Override
    public boolean unLock(String key) {
        try {
            String redisKey = getKey(key);
            List<String> keys = Arrays.asList(redisKey, LOCAL_REQUEST_IDS.get());
            logger.debug("unlock :::: redisKey:{},redisVal:{}", redisKey, LOCAL_REQUEST_IDS.get());
            Long result = (Long) redisTemplate.execute(RELEASE_LOCK_SCRIPT, keys);
            if (!ObjectUtils.isEmpty(result) && result > 0) {
                logger.info("thread name :{} ,release lock :{} success, Status code reply={}", Thread.currentThread().getName(), key, result);
                return true;
            } else if (!ObjectUtils.isEmpty(result) && result == -1) {
                logger.warn("thread name :{} ,release lock :{} has expired or released. Status code reply={}", Thread.currentThread().getName(), key, result);
            } else {
                logger.error("thread name :{} ,release lock :{} failed, del key failed. Status code reply={}", Thread.currentThread().getName(), key, result);
            }
        } catch (Exception e) {
            logger.error("thread name :{} ,release lock occured an exception:{}", Thread.currentThread().getName(), e.getMessage(), e);
        } finally {
            LOCAL_REQUEST_IDS.remove();
        }
        return false;
    }

    private String getKey(String key) {
        String redisKey = buildKey(key, this.environment);
        //如果都是空那就抛出异常
        if (StringUtils.isEmpty(redisKey)) {
            throw new RuntimeException("key is null");
        }
        return redisKey;
    }

}
