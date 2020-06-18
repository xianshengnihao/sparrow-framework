package com.sina.sparrowframework.sinacloud;

import com.sina.sparrowframework.tools.utility.JsonUtils;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;

/**
 * 文件上传到去的结果
 * created  on 2018/10/12.
 */
public class StoreResult {

    /**
     * 在去上的路径,不以 {@code /} 开头
     */
    @NonNull
    private String path;

    /**
     * 文件过期时间,如果是永久存储则为 null
     */
    private LocalDateTime expiredTime;

    @NonNull
    private String name;

    /**
     * 使用 base64 编码
     */
    @NonNull
    private String md5;

    /**
     * 是否是 zip 文件
     */
    private boolean compress;

    @NonNull
    public String getPath() {
        return path;
    }

    public StoreResult setPath(@NonNull String path) {
        this.path = path;
        return this;
    }

    public LocalDateTime getExpiredTime() {
        return expiredTime;
    }

    public StoreResult setExpiredTime(LocalDateTime expiredTime) {
        this.expiredTime = expiredTime;
        return this;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public StoreResult setName(@NonNull String name) {
        this.name = name;
        return this;
    }

    @NonNull
    public String getMd5() {
        return md5;
    }

    public StoreResult setMd5(@NonNull String md5) {
        this.md5 = md5;
        return this;
    }

    public boolean isCompress() {
        return compress;
    }

    public StoreResult setCompress(boolean compress) {
        this.compress = compress;
        return this;
    }

    @Override
    public String toString() {
        return JsonUtils.writeToJson( this, true );
    }
}
