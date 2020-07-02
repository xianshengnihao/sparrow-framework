package com.sina.sparrowframework.sinacloud;

import com.sina.sparrowframework.tools.utility.JsonUtils;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;

import javax.validation.constraints.Size;
import java.io.InputStream;
import java.util.Map;

/**
 * created  on 2018/10/12.
 */
public class StoreForm {

    /**
     * 上传文件的目标bucket
     */
    private String bucket;

    private MediaType mediaType;

    /**
     * 上传文件的输入流
     */
    private InputStream inputStream;

    /**
     * 文件名(原始文件名)
     */
    private String name;

    /**
     * 上传文件的唯一键，如：/path/test.txt
     */
    private String key;

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

    public String getBucket() {
        return bucket;
    }

    public StoreForm setBucket(String bucket) {
        this.bucket = bucket;
        return this;
    }

    public String getName() {
        return name;
    }

    public StoreForm setName(String name) {
        this.name = name;
        return this;
    }

    public String getKey() {
        return key;
    }

    public StoreForm setKey(String key) {
        this.key = key;
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
