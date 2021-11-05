package com.sina.sparrowframework.sinacloud;

import com.sina.cloudstorage.SCSClientException;
import com.sina.cloudstorage.services.scs.SCS;
import com.sina.cloudstorage.services.scs.model.*;
import com.sina.sparrowframework.exception.business.DataException;
import com.sina.sparrowframework.metadata.constants.BaseCode;
import com.sina.sparrowframework.tools.struct.ResultCode;
import com.sina.sparrowframework.tools.tuple.Pair;
import com.sina.sparrowframework.tools.utility.*;
import org.apache.commons.codec.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.Key;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.sina.sparrowframework.tools.utility.Assert.*;

/**
 * 这个类是 {@link CloudStore} 一个实现
 * created  on 2018/10/12.
 */
public final class SinaCloudStore implements CloudStore, EnvironmentAware {

    private static final Logger LOG = LoggerFactory.getLogger(SinaCloudStore.class);

    private static final String DEFAULT_MAX_CACHE_TIME = "tasty.sina.cloud.store.max.cache.time";

    private static final String CIPHER_AES_KEY = "tasty.sina.cloud.store.cipher.aes.version.%s.key";

    private static final String PATH_PREFIX = "tasty.sina.cloud.store.path.prefix";

    private static final String BUCKET = "tasty.sina.cloud.store.%s.bucket";

    /**
     * 必须以 {@code /} 结束
     */
    private static final String CLOUD_DOWNLOAD_HOST = "tasty.sina.cloud.store.download.host";

    /**
     * 当前 版本
     */
    private static final String CIPHER_AES_KEY_VERSION = "tasty.sina.cloud.store.cipher.aes.key.version";


    private static final String HEADER_EXPIRED_TIME = "x-sina-expire";

    private static final String HEADER_ACL = "x-amz-acl";


    private static final String META_FILE_NAME = "file-name";

    private static final String META_ORIGINAL_NAME = "original-name";

    private static final String META_CIPHER_VERSION = "cipher-version";

    private static final String META_UPLOAD_TIME = "upload-time";

    private static final String META_ACCEPT_MODE = "accept-mode";

    private static final String META_CACHE = "cache";

    private static final String META_LENGTH = "length";

    private static final String META_COMPRESS = "compress";

    private static final String META_MD5 = "md5";

    private static final String META_MEDIA_TYPE = "mediatype";

    private static final String META_CONTENT_DISPOSITION = "contentdisposition";


    private static final String META_PREFIX = "custom-";

    private static final Pattern WORD_PATTERN = Pattern.compile("[\\w]+");

    private static final Pattern URL_SSIG_PATTERN = Pattern.compile("(?<=ssig=).*?(?=&)");

    private static final Pattern URL_KID_PATTERN = Pattern.compile("(?<=KID=).*?(?=&)");

    private static final String END_OFFSET = "jelly@cloud#_&#$end";

    private static final ListResult EMPTY_LIST = new ListResult(END_OFFSET, true, Collections.emptyList());

    private SCS sinaScs;

    private Environment env;

    @Override
    public void setEnvironment(Environment environment) {
        env = environment;
    }

    @Override
    public CloudMeta upload(StoreForm form) throws CloudStoreException, DataException {
        assertStoreForm(form);
        final long start = System.currentTimeMillis();

        Integer version = env.getProperty(CIPHER_AES_KEY_VERSION, Integer.class, 1);
        // 缓存文件,若需要加密则加密文件
        File file = cacheAndEncryptFileIfNeed(form, version);
        if (!form.isCipher()) {
            version = null;
        }
        // 创建元数据
        ObjectMetadata meta = crateMetaAndStoreResult(form, file, version);

        String fileKey = form.getKey();

        try {
            // 执行上传
            PutObjectResult putObjectResult;
            putObjectResult = sinaScs.putObject(new PutObjectRequest(form.getBucket(), fileKey, file).withMetadata(meta));

            String path = form.getBucket().concat("/").concat(fileKey);

            LOG.info("tRestoreExpirationTime:{}", putObjectResult.getExpirationTime());

            meta.setRestoreExpirationTime(putObjectResult.getExpirationTime());

            LOG.info("上传路径:{},cost[{}]ms", path, System.currentTimeMillis() - start);

            return createMeta(path, meta, CloudMeta.class);
        } catch (SCSClientException e) {
            throw new CloudStoreException(BaseCode.ASSERT_ERROR, e, e.getMessage());
        } finally {
            try {
                Files.delete(file.toPath());
            } catch (IOException e) {
                LOG.error("", e);
            }
        }

    }


    @Override
    public DownloadResult download(String path) throws CloudStoreException, DataException {
        hasText(path, "path required");
        final long start = System.currentTimeMillis();

        Pair<String, String> pair = parseBucketAndFileKey(path);

        try {
            S3Object fileObj;
            // 执行下载
            fileObj = sinaScs.getObject(pair.getLeft(), pair.getRight());

            LOG.info("下载路径:{},cost[{}]ms", pair, System.currentTimeMillis() - start);

            return createResult(fileObj);
        } catch (SCSClientException e) {
            throw new CloudStoreException(BaseCode.ASSERT_ERROR, e, e.getMessage());
        }
    }

    @Override
    public boolean exists(String path) throws CloudStoreException, DataException {
        hasText(path, "path required");
        final long start = System.currentTimeMillis();

        Pair<String, String> pair = parseBucketAndFileKey(path);
        boolean exists;
        try {
            sinaScs.getObjectMetadata(pair.getLeft(), pair.getRight());

            LOG.info("元数据访问路径:{},cost[{}]ms", pair, System.currentTimeMillis() - start);
            exists = true;
        } catch (SCSClientException e) {
            exists = false;
        }
        return exists;
    }

    @Override
    public CloudMeta meta(String path) throws CloudStoreException, DataException {
        hasText(path, "path required");
        final long start = System.currentTimeMillis();

        Pair<String, String> pair = parseBucketAndFileKey(path);
        try {
            ObjectMetadata meta;

            meta = sinaScs.getObjectMetadata(pair.getLeft(), pair.getRight());
            LOG.info("meta:{}", JsonUtils.writeToJson(meta, true));
            LOG.info("元数据下载路径:{},cost[{}]ms", pair.getRight(), System.currentTimeMillis() - start);
            return createMeta(path, meta, CloudMeta.class);
        } catch (SCSClientException e) {
            throw new CloudStoreException(BaseCode.ASSERT_ERROR, e, "meta get meta error,path[%s]", pair);
        }
    }

    @Override
    public String getUrl(String path) throws CloudStoreException, DataException {
        String url;
        if (exists(path)) {
            url = doGetUrl(path);
        } else {
            throw new CloudStoreException(BaseCode.ASSERT_ERROR);
        }
        return url;
    }


    @Override
    @NonNull
    public ListResult listPath(String prefix, String offset, int rowCount) throws CloudStoreException, DataException {
        hasText(prefix, "prefix required");
        if (rowCount > 500 || rowCount < 1) {
            throw new DataException(BaseCode.ASSERT_ERROR, "rowCount error");
        }

        if (END_OFFSET.equals(offset)) {
            return EMPTY_LIST;
        }
        Pair<String, String> pair = parseBucketAndFileKey(prefix);
        ListObjectsRequest form = new ListObjectsRequest()

                .withBucketName(pair.getLeft())
                .withPrefix(pair.getRight())
                .withMarker(offset)
                .withMaxKeys(rowCount);
        ListResult listResult;

        ObjectListing listing;

        try {
            // 请求云存储.
            listing = sinaScs.listObjects(form);

            if (listing == null) {
                listResult = EMPTY_LIST;
            } else {
                listResult = createListResult(pair.getLeft(), listing);
            }
            return listResult;
        } catch (SCSClientException e) {
            throw new CloudStoreException(BaseCode.ASSERT_ERROR, e, e.getMessage());
        }
    }

    @Override
    public String wrapUrl(String url, Long express) throws CloudStoreException, DataException {
        assertNotNull(url, "url not be null");

        express = (express == null || express < 0) ? 0L : express;
        Date date = new Date(System.currentTimeMillis() + express);
        URL u = sinaScs.generatePresignedUrl(getBucketWithAcceptMode(AcceptMode.PRIVATE), url, date, Boolean.FALSE);
        try {
            return patternUrlKid(patternUrlSsig(u.toString()));
        } catch (UnsupportedEncodingException e) {
            throw new CloudStoreException(BaseCode.ASSERT_ERROR, e, e.getMessage());
        }
    }

    private static String patternUrlSsig(String url) throws UnsupportedEncodingException {
        Matcher matcher = URL_SSIG_PATTERN.matcher(url);
        if (matcher.find()) {
            String str = matcher.group(0);
            url = url.replace(str, URLEncoder.encode(str, Charsets.UTF_8.displayName()));
        }
        return url;
    }

    private static String patternUrlKid(String url) throws UnsupportedEncodingException {
        Matcher matcher = URL_KID_PATTERN.matcher(url);
        if (matcher.find()) {
            String kidStr = matcher.group(0);
            String decodeUrl = URLDecoder.decode(kidStr, Charsets.UTF_8.displayName());
            url = url.replace(kidStr, URLEncoder.encode(decodeUrl, Charsets.UTF_8.displayName()));
        }
        return url;
    }

    private ListResult createListResult(final String bucket, ObjectListing listing) {
        List<S3ObjectSummary> list = listing.getObjectSummaries();
        List<String> keyList = new ArrayList<>(list.size());

        for (S3ObjectSummary summary : list) {
            keyList.add(bucket + "/" + summary.getKey());
        }

        ListResult listResult;
        if (listing.getNextMarker() == null) {
            listResult = new ListResult(END_OFFSET, true, keyList);
        } else {
            listResult = new ListResult(listing.getNextMarker(), false, keyList);
        }
        return listResult;
    }

    private String doGetUrl(String path) {
        return env.getRequiredProperty(CLOUD_DOWNLOAD_HOST).concat(path);
    }

    private String generateFileKey() {
        return LocalDate.now().format(DateUtil.SLASH_DATE_FORMATTER) + "/" + UUIDUtils.randomUUIDWithNoDash();
    }

    private void assertStoreForm(StoreForm form) throws DataException {
        assertNotNull(form, "form required");
        assertNotNull(form.getMediaType(), "mediaType required");

        assertNotNull(form.getBucket(), "bucket can't be null");
        assertNotNull(form.getKey(), "key can't be null");
        assertNotNull(form.getInputStream(), "inputStream can't be null");
        assertNotNull(form.getName(), "name can't be null");

        if (form.getMeta() != null) {
            assertTrue(form.getMeta().size() <= 20, "meta size must less or equals then 20");
            assertMeta(form.getMeta());
        }
    }

    private void assertMeta(Map<String, String> meta) throws DataException {
        for (Map.Entry<String, String> e : meta.entrySet()) {
            if (e.getKey() == null) {
                continue;
            }
            assertTrue(WORD_PATTERN.matcher(e.getKey()).matches(), "key must word or numbers");
        }
    }


    private File cacheAndEncryptFileIfNeed(StoreForm form, Integer version) throws CloudStoreException {
        try {
            File file = StreamUtils.copyToFileAndClose(form.getInputStream());
            // 记录文件大小
            form.setLength(file.length());
            if (form.isCipher()) {
                // 记录原文件 md5
                form.setMd5(DigestUtils.md5Base64(file));
                file = encryptFile(new FileSystemResource(file), version);
            }
            return file;
        } catch (IOException e) {
            throw new CloudStoreException(BaseCode.ASSERT_ERROR, e, e.getMessage());
        }
    }


    private File encryptFile(Resource resource, Integer version) throws CloudStoreException {
        try {

            String keyStr = String.format(CIPHER_AES_KEY, version);

            Key aesKey = KeyUtils.readAesKey(env.getRequiredProperty(keyStr));
            return CipherUtils.encrypt(aesKey, CipherUtils.Algorithm.AES, resource.getInputStream());
        } catch (Exception e) {
            throw new CloudStoreException(BaseCode.ASSERT_ERROR, e, e.getMessage());
        }
    }

    private InputStream decryptFile(InputStream source, Integer version) {
        try {
            String keyStr = String.format(CIPHER_AES_KEY, version);
            Key aesKey = KeyUtils.readAesKey(env.getRequiredProperty(keyStr));
            final File file = CipherUtils.decrypt(aesKey, CipherUtils.Algorithm.AES, source);

            return new FileInputStream(file) {
                @Override
                public void close() throws IOException {
                    try {
                        super.close();
                    } finally {
                        if (file.exists() && file.delete()) {
                            LOG.trace("删除解码后的临时文件");
                        }
                    }

                }
            };
        } catch (Exception e) {
            throw new CloudStoreException(BaseCode.ASSERT_ERROR, e, e.getMessage());
        }
    }


    private ObjectMetadata crateMetaAndStoreResult(final StoreForm form, final File file
            , final Integer keyVersion) {
        assertGtZero(form.getLength(), "文件长度为0");

        ObjectMetadata meta = new ObjectMetadata();

        final String md5 = createMd5(file);

        if (!StrToolkit.hasText(form.getMd5())) {
            // 没有 md5 值,说明文件未加密
            form.setMd5(md5);
        }

        final ZonedDateTime now = ZonedDateTime.now(TimeUtils.ZONE8);

        final String contentDisposition = createContentDisposition(form, now);
        meta.setContentDisposition(contentDisposition);
        meta.setContentType(form.getMediaType().toString());
        meta.setContentLength(file.length());
        meta.setContentMD5(md5);

        // 设置文件元数据
        meta.setUserMetadata(createFileMeta(form, keyVersion, now));
        return meta;
    }

    private String createContentDisposition(final StoreForm form, final ZonedDateTime now) {
        ContentDisposition.Builder builder;
        builder = ContentDisposition
                .builder(form.isAttachment() ? "attachment" : "inline")
                .size(form.getLength())
                .creationDate(now)
                .modificationDate(now)
        ;

        if (form.isAttachment()) {
            builder.filename(form.getName(), StandardCharsets.UTF_8);
        }
        return builder.build().toString();
    }

    @Deprecated
    private String getBucketWithAcceptMode(AcceptMode acceptMode) {
        return env.getRequiredProperty(String.format(BUCKET, acceptMode.name().toLowerCase()));
    }


    private Map<String, String> createFileMeta(final StoreForm form,
                                               final Integer keyVersion, final ZonedDateTime now) {
        Map<String, String> metaMap = new HashMap<>();

        metaMap.put(META_CIPHER_VERSION, keyVersion == null ? "" : String.valueOf(keyVersion));
        metaMap.put(META_UPLOAD_TIME, now.format(TimeUtils.DATETIME_FORMATTER));
        metaMap.put(META_LENGTH, String.valueOf(form.getLength()));

        metaMap.put(META_MD5, String.valueOf(form.getMd5()));
        metaMap.put(META_MEDIA_TYPE, encodeBase64(form.getMediaType().toString()));
        metaMap.put(META_ORIGINAL_NAME, encodeBase64(form.getName()));

        if (form.getMeta() != null) {
            for (Map.Entry<String, String> e : form.getMeta().entrySet()) {
                if (e.getKey() == null || e.getValue() == null) {
                    continue;
                }
                metaMap.put(encodeText(META_PREFIX.concat(e.getKey())), encodeText(e.getValue()));
            }
        }
        return metaMap;

    }

    private String createMd5(File file) throws CloudStoreException {
        try {
            return DigestUtils.md5Base64(file);
        } catch (IOException e) {
            throw new CloudStoreException(BaseCode.ASSERT_ERROR);
        }
    }

    private String decodeText(String text) {
        try {
            return URLDecoder.decode(text, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private String encodeText(String text) {
        try {
            return URLEncoder.encode(text, StandardCharsets.UTF_8.name()).toLowerCase();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param path 不以 {@code /} 开头
     * @return pair ,first 为 bucket ,second 为 fileKey
     */
    private Pair<String, String> parseBucketAndFileKey(String path) throws CloudStoreException {
        try {
            String bucket, fileKey;
            int index = path.indexOf('/');
            if (index > 0) {
                bucket = path.substring(0, index);
                fileKey = path.substring(index + 1);
            } else {
                bucket = path;
                fileKey = "";
            }
            return Pair.of(bucket, fileKey);
        } catch (IndexOutOfBoundsException e) {
            throw new CloudStoreException(BaseCode.ASSERT_ERROR);
        }
    }

    private DownloadResult createResult(S3Object fileObj) {
        ObjectMetadata meta = fileObj.getObjectMetadata();
        Map<String, String> metaMap = meta.getUserMetadata();

        String versionStr = metaMap.get(META_CIPHER_VERSION);
        Integer version = null;
        if (StrToolkit.hasText(versionStr)) {
            try {
                version = Integer.parseInt(versionStr);
            } catch (NumberFormatException e) {
                // 忽略
            }
        }
        String path = fileObj.getBucketName() + "/" + fileObj.getKey();
        // 创建 元数据
        DownloadResult result = createMeta(path, meta, DownloadResult.class);
        // 设置文件内容
        result.setInputStream(createInputStream(fileObj.getObjectContent(), version));

        return result;
    }

    private Map<String, String> createCustomMeta(Map<String, String> metaMap) {
        Map<String, String> map = new HashMap<>((int) (metaMap.size() / 0.75f));
        String key;
        for (Map.Entry<String, String> e : metaMap.entrySet()) {
            if (e.getKey().startsWith(META_PREFIX)) {
                key = e.getKey().substring(META_PREFIX.length());
                map.put(decodeText(key), decodeText(e.getValue()));
            }
        }
        return map;
    }

    private InputStream createInputStream(InputStream source, Integer version) {
        InputStream in = source;
        if (version != null) {
            in = decryptFile(source, version);
        }
        return in;
    }


    private <T extends CloudMeta> T createMeta(String path, ObjectMetadata meta, Class<T> clazz)
            throws CloudStoreException {
        try {
            Map<String, String> metaMap = meta.getUserMetadata();
            Date date = meta.getRestoreExpirationTime();
            LocalDateTime expiredTime;
            if (date != null) {
                expiredTime = LocalDateTime.from(date.toInstant());
            } else {
                expiredTime = TimeUtils.SOURCE_DATE_TIME;
            }
            T t = BeanUtils.instantiateClass(clazz);

            ContentDisposition disposition = meta.getContentDisposition() == null
                    ? null : ContentDisposition.parse(meta.getContentDisposition());

            t.setMediaType(getMediaType(meta, clazz))
                    .setCache(Boolean.parseBoolean(metaMap.get(META_CACHE)))
                    .setExpiredTime(expiredTime)
                    .setLength(meta.getContentLength())
                    .setMd5(metaMap.getOrDefault(META_MD5, ""))
                    .setContentDisposition(disposition)
                    .setUploadTime(getUploadTime(metaMap.get(META_UPLOAD_TIME)))
                    .setMeta(createCustomMeta(metaMap))
                    .setUrl(doGetUrl(path))
                    .setPath(path)
                    .setLength(getFileLength(metaMap))
                    .setOriginalName(decodeBase64(metaMap.get(META_ORIGINAL_NAME)));
            return t;
        } catch (Exception e) {
            throw new CloudStoreException(BaseCode.ASSERT_ERROR, e, e.getMessage());
        }
    }

    private LocalDateTime getUploadTime(String timeText) {
        LocalDateTime time;
        if (StrToolkit.hasText(timeText)) {
            time = LocalDateTime.parse(timeText, TimeUtils.DATETIME_FORMATTER);
        } else {
            time = TimeUtils.SOURCE_DATE_TIME;
        }
        return time;
    }

    private long getFileLength(Map<String, String> metaMap) {
        String text = metaMap.get(META_LENGTH);
        long length = -1;
        if (StrToolkit.hasText(text)) {
            length = Long.parseLong(text);
        }
        return length;
    }

    private MediaType getMediaType(ObjectMetadata meta, Class<?> clazz) {
        String text = meta.getUserMetadata().get(META_MEDIA_TYPE);
        return MediaType.valueOf(decodeBase64(text));
    }

    /**
     * 新浪云存储有字符串限制所以需要编码
     */
    private String encodeBase64(@NonNull String text) {
        return org.apache.commons.codec.binary.Base64.encodeBase64String(text.getBytes(StandardCharsets.UTF_8));
    }

    private String decodeBase64(@NonNull String ciphertext) {
        return new String(
                org.apache.commons.codec.binary.Base64.decodeBase64(ciphertext.getBytes(StandardCharsets.UTF_8))
                , StandardCharsets.UTF_8
        );
    }

    public void setSinaScs(SCS sinaScs) {
        this.sinaScs = sinaScs;
    }

}

