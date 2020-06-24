package com.sina.sparrowframework.redis.lock;

/**
 * 锁基本属性
 *
 * @author tianye6
 * @date 2019/4/29 15:00
 */
public class LockProperties {

    /**
     * 单位ms，加锁操作持有锁的最大时间
     * 默认：10分钟
     */
    private Long expiredTime = 1000 * 60 * 10L;

    /**
     * 获取锁的重试次数
     * 默认：10
     */
    private Integer retryCnt = 1;

    /**
     * 单位ms，睡眠等待时间
     * 默认：1000
     */
    private Long sleepTime = 1000L;
    public  LockProperties () {

    }
    public LockProperties(Integer retryCnt) {
        this.retryCnt = retryCnt;
    }

    public LockProperties(Integer retryCnt,Long expiredTime) {
        this.expiredTime = expiredTime;
        this.retryCnt = retryCnt;
    }

    public LockProperties(Integer retryCnt,Long expiredTime, Long sleepTime) {
        this.expiredTime = expiredTime;
        this.retryCnt = retryCnt;
        this.sleepTime = sleepTime;
    }

    public Long getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(Long expiredTime) {
        this.expiredTime = expiredTime;
    }

    public Integer getRetryCnt() {
        return retryCnt;
    }

    public void setRetryCnt(Integer retryCnt) {
        this.retryCnt = retryCnt;
    }

    public Long getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(Long sleepTime) {
        this.sleepTime = sleepTime;
    }
}
