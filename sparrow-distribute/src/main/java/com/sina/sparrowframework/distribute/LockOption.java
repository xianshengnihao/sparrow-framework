package com.sina.sparrowframework.distribute;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.sina.sparrowframework.tools.utility.Assert.hasText;

/**
 * created  on 2018/9/8.
 */
public class LockOption {

    private static final long DEFAULT_SECOND = 30L;

    /**
     * 用于唯一标的分布式锁
     */
    public final String key;

    public final List<String> keyList;

    /**
     * 尝试获取锁的持有者唯一标识
     */
    public final String holder;

    /**
     * 表示 在 second 后 锁自动释放
     */
    private long second = DEFAULT_SECOND;

    private String outCache;


    public LockOption(String key, String holder) {
        hasText( key, "key required" );
        hasText( holder, "holder required" );
        this.key = key;
        this.holder = holder;
        keyList = Collections.singletonList( this.key );
    }

    public LockOption(String key) {
        this( key, UUID.randomUUID().toString() );
    }

    public String getKey() {
        return key;
    }

    public String getHolder() {
        return holder;
    }

    public long getSecond() {
        if (second < 1L) {
            second = DEFAULT_SECOND;
        }
        return second;
    }

    public void setSecond(long second) {
        this.second = second;
    }

    @Override
    public String toString() {
        if (outCache == null) {
            outCache = new ToStringBuilder( this )
                    .append( "key", key )
                    .append( "keyList", keyList )
                    .append( "holder", holder )
                    .append( "second", second )
                    .toString();
        }
        return outCache;

    }
}
