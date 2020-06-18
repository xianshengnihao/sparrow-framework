package com.sina.sparrowframework.sinacloud;

import com.sina.sparrowframework.tools.utility.JsonUtils;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;

import javax.validation.constraints.Size;
import java.io.File;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * created  on 2018/10/12.
 */
public class StoreForm {

    /**
     * file 和 {@link #inputStream} 共同存在时优先使用 file
     */
    private File file;

    @NonNull
    private AcceptMode acceptMode;

    @NonNull
    private MediaType mediaType;

    /**
     * 自定义前缀,不能包含 {@code /}
     */
    private String prefix;

    /**
     * file 与 inputStream 不能同时为 null
     * inputStream 和 {@link #file} 共同存在时优先使用 file
     */
    private InputStream inputStream;

    /**
     * 文件名
     */
    private String name;

    /**
     * 在 time 时间 后,文件过期.
     */
    private Long time;

    /**
     * 当 time 不为 null 时,此字段也不为 null
     */
    private TimeUnit timeUnit;

    /**
     * 允许访问文件时,网络结点进行缓存
     */
    private boolean cache;

    /**
     * 加密存储
     */
    private boolean cipher;

    /**
     * 是附件吗, 如果为 true ,则用浏览器访问时会自动下载到本地
     */
    private boolean attachment;

    /**
     * 文件长度,仅内部 实现使用
     */
    private long length;

    /**
     * 文件长度,仅内部 实现使用
     */
    private String md5;


    /**
     * 自定义元数据. 下载时原值返回。
     * 最多20个元素. 其中 key 只支持英文和数字
     */
    @Size(max = 20)
    private Map<String, String> meta;


    public File getFile() {
        return file;
    }

    public StoreForm setFile(File file) {
        this.file = file;
        return this;
    }

    @NonNull
    public AcceptMode getAcceptMode() {
        return acceptMode;
    }

    public StoreForm setAcceptMode(@NonNull AcceptMode acceptMode) {
        this.acceptMode = acceptMode;
        return this;
    }

    @NonNull
    public MediaType getMediaType() {
        return mediaType;
    }

    public StoreForm setMediaType(@NonNull MediaType mediaType) {
        this.mediaType = mediaType;
        return this;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public StoreForm setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
        return this;
    }

    public String getName() {
        return name;
    }

    public StoreForm setName(String name) {
        this.name = name;
        return this;
    }

    public Long getTime() {
        return time;
    }

    public StoreForm setTime(Long time) {
        this.time = time;
        return this;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public StoreForm setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
        return this;
    }

    public boolean isCache() {
        return cache;
    }

    public StoreForm setCache(boolean cache) {
        this.cache = cache;
        return this;
    }

    public boolean isCipher() {
        return cipher;
    }

    public StoreForm setCipher(boolean cipher) {
        this.cipher = cipher;
        return this;
    }

    public boolean isAttachment() {
        return attachment;
    }

    public StoreForm setAttachment(boolean attachment) {
        this.attachment = attachment;
        return this;
    }

    public Map<String, String> getMeta() {
        return meta;
    }

    public StoreForm setMeta(Map<String, String> meta) {
        this.meta = meta;
        return this;
    }

    public String getPrefix() {
        return prefix;
    }

    public StoreForm setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    /**
     * 仅供内部实现使用
     */
    long getLength() {
        return length;
    }

    /**
     * 仅供内部实现使用
     */
    StoreForm setLength(long length) {
        this.length = length;
        return this;
    }


    /**
     * 仅供内部实现使用
     */
    String getMd5() {
        return md5;
    }


    /**
     * 仅供内部实现使用
     */
    StoreForm setMd5(String md5) {
        this.md5 = md5;
        return this;
    }

    @Override
    public String toString() {
        return JsonUtils.writeToJson( this, true );
    }
}
