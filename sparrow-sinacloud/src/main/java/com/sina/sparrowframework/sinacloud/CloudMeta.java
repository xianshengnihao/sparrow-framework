package com.sina.sparrowframework.sinacloud;

import com.sina.sparrowframework.tools.utility.JsonUtils;
import org.springframework.http.ContentDisposition;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * created  on 2018/10/12.
 */
public class CloudMeta {

    @NonNull
    private String path;

    /**
     * 原始文件名
     */
    @NonNull
    private String originalName;

    /**
     * 若不是由 {@link CloudStore} 上传则为 {@link com.sina.sparrowframework.tools.utility.TimeUtils#SOURCE_DATE_TIME}
     */
    private LocalDateTime expiredTime;

    @NonNull
    private MediaType mediaType;

    /**
     * 若不是由 {@link CloudStore} 上传则为 {@code ""}
     */
    @NonNull
    private String md5;

    private boolean cache;

    /**
     * 若不是由 {@link CloudStore} 上传则小于 0
     */
    private long length;

    @NonNull
    private String url;


    /**
     * 若不是由 {@link CloudStore} 上传则为 {@link com.sina.sparrowframework.tools.utility.TimeUtils#SOURCE_DATE_TIME}
     */
    @NonNull
    private LocalDateTime uploadTime;

    /**
     * 若不是由 {@link CloudStore} 上传则为 {@code null}
     */
    @Nullable
    private ContentDisposition contentDisposition;


    private LocalDateTime updateTime;

    /**
     * 自定义元数据. 原值返回
     */
    @NonNull
    private Map<String, String> meta;

    @NonNull
    public String getPath() {
        return path;
    }

    public CloudMeta setPath(@NonNull String path) {
        this.path = path;
        return this;
    }

    @NonNull
    public String getOriginalName() {
        return originalName;
    }

    public CloudMeta setOriginalName(@NonNull String originalName) {
        this.originalName = originalName;
        return this;
    }

    public LocalDateTime getExpiredTime() {
        return expiredTime;
    }

    public CloudMeta setExpiredTime(LocalDateTime expiredTime) {
        this.expiredTime = expiredTime;
        return this;
    }

    @NonNull
    public MediaType getMediaType() {
        return mediaType;
    }

    public CloudMeta setMediaType(@NonNull MediaType mediaType) {
        this.mediaType = mediaType;
        return this;
    }

    @NonNull
    public String getMd5() {
        return md5;
    }

    public CloudMeta setMd5(@NonNull String md5) {
        this.md5 = md5;
        return this;
    }

    public boolean isCache() {
        return cache;
    }

    public CloudMeta setCache(boolean cache) {
        this.cache = cache;
        return this;
    }

    public long getLength() {
        return length;
    }

    public CloudMeta setLength(long length) {
        this.length = length;
        return this;
    }

    @NonNull
    public String getUrl() {
        return url;
    }

    public CloudMeta setUrl(@NonNull String url) {
        this.url = url;
        return this;
    }

    @NonNull
    public LocalDateTime getUploadTime() {
        return uploadTime;
    }

    public CloudMeta setUploadTime(@NonNull LocalDateTime uploadTime) {
        this.uploadTime = uploadTime;
        return this;
    }


    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public CloudMeta setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    @NonNull
    public Map<String, String> getMeta() {
        return meta;
    }

    public CloudMeta setMeta(@NonNull Map<String, String> meta) {
        this.meta = meta;
        return this;
    }

    @Nullable
    public ContentDisposition getContentDisposition() {
        return contentDisposition;
    }

    public CloudMeta setContentDisposition(@Nullable ContentDisposition contentDisposition) {
        this.contentDisposition = contentDisposition;
        return this;
    }

    @Override
    public String toString() {
        return JsonUtils.writeToJson( this, true );
    }
}
