package com.sina.sparrowframework.password.xiaodai.support;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 由于gson在将json反序列化为Object时对Numberic默认序列化为double类型，可能会出现不符合预期的转型异常。
 * 因此废弃所有返回类型为Map<String, Object>类型的方法。
 * <p>
 * 对返回的json串处理由调用方依照http接口定义自行处理。
 * ps.建议定义接口返回类型bean,使用
 * T XiaodaiSupportJsonUtil.defaultGson().fromJson(String json, Class<T> clz);
 * 直接序列化为返回对象。
 *
 * @author Liu Chang
 * @date 2019/10/11
 */
public class XiaodaiSupportBaseHttpClient {

    private static final String MODULE_NAME = "小贷Xxx系统：";
    public static final String REQ_ID = "req.id";

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static final XiaodaiSupportBaseHttpClient WEIBO_HTTP_CLIENT = new XiaodaiSupportBaseHttpClient(1000, 2);

    public static final XiaodaiSupportBaseHttpClient WEIBO_FLEXIBLE_CLIENT = new XiaodaiSupportBaseHttpClient(1000, 2);


    /**
     * 服务的Host
     */
    private String serviceUrlPrefix;

    /**
     * OkHttp默认值
     */
    private Integer connectTimeoutMillis = 800;

    /**
     * OkHttp默认值
     */
    private Integer readTimeoutMillis = 800;

    /**
     * OkHttp默认值
     */
    private Integer writeTimeoutMillis = 800;

    /**
     * 重试次数
     */
    private Integer retryTimes = 0;

    private OkHttpClient okHttpClient;

    private Retrofit retrofit;

    /**
     * 构造方法
     */
    public XiaodaiSupportBaseHttpClient() {
        init();
    }

    /**
     * 构造方法
     *
     * @param timeoutMillis
     * @param retryTimes
     */
    public XiaodaiSupportBaseHttpClient(Integer timeoutMillis, Integer retryTimes) {

        // check
        Preconditions.checkArgument(timeoutMillis > 0, "connect timeout mills is less than zero!");
        Preconditions.checkArgument(retryTimes > 0, "retryTimes is less than zero!");

        this.connectTimeoutMillis = timeoutMillis;
        this.readTimeoutMillis = timeoutMillis;
        this.writeTimeoutMillis = timeoutMillis;
        this.retryTimes = retryTimes;

        init();
    }

    /**
     * 构造方法
     *
     * @param connectTimeoutMillis
     * @param writeTimeoutMillis
     * @param writeTimeoutMillis
     */
    public XiaodaiSupportBaseHttpClient(Integer connectTimeoutMillis, Integer readTimeoutMillis, Integer writeTimeoutMillis) {

        // check
        Preconditions.checkArgument(connectTimeoutMillis > 0, "connect timeout mills is less than zero!");
        Preconditions.checkArgument(readTimeoutMillis > 0, "read timeout mills is less than zero!");
        Preconditions.checkArgument(writeTimeoutMillis > 0, "write timeout mills is less than zero!");

        this.connectTimeoutMillis = connectTimeoutMillis;
        this.readTimeoutMillis = readTimeoutMillis;
        this.writeTimeoutMillis = writeTimeoutMillis;

        init();
    }

    /**
     * 构造方法
     *
     * @param connectTimeoutMillis
     * @param writeTimeoutMillis
     * @param writeTimeoutMillis
     */
    public XiaodaiSupportBaseHttpClient(Integer connectTimeoutMillis, Integer readTimeoutMillis, Integer writeTimeoutMillis, Boolean noRetry) {

        // check
        Preconditions.checkArgument(connectTimeoutMillis > 0, "connect timeout mills is less than zero!");
        Preconditions.checkArgument(readTimeoutMillis > 0, "read timeout mills is less than zero!");
        Preconditions.checkArgument(writeTimeoutMillis > 0, "write timeout mills is less than zero!");

        this.connectTimeoutMillis = connectTimeoutMillis;
        this.readTimeoutMillis = readTimeoutMillis;
        this.writeTimeoutMillis = writeTimeoutMillis;

        if (noRetry) {
            initNoRetry();
        } else {
            init();
        }
    }

    /**
     * 服务前缀
     *
     * @param serviceUrlPrefix
     * @param connectTimeoutMillis
     * @param writeTimeoutMillis
     * @param writeTimeoutMillis
     */
    public XiaodaiSupportBaseHttpClient(String serviceUrlPrefix, Integer connectTimeoutMillis, Integer readTimeoutMillis, Integer writeTimeoutMillis) {

        // check
        Preconditions.checkArgument(!Strings.isNullOrEmpty(serviceUrlPrefix), "server host is empty!");
        Preconditions.checkArgument(connectTimeoutMillis > 0, "connect timeout mills is less than zero!");
        Preconditions.checkArgument(readTimeoutMillis > 0, "read timeout mills is less than zero!");
        Preconditions.checkArgument(writeTimeoutMillis > 0, "write timeout mills is less than zero!");

        this.serviceUrlPrefix = serviceUrlPrefix;
        this.connectTimeoutMillis = connectTimeoutMillis;
        this.readTimeoutMillis = readTimeoutMillis;
        this.writeTimeoutMillis = writeTimeoutMillis;

        init();
    }

    /**
     * 服务前缀
     *
     * @param serviceUrlPrefix
     * @param connectTimeoutMillis
     * @param writeTimeoutMillis
     * @param writeTimeoutMillis
     */
    public XiaodaiSupportBaseHttpClient(String serviceUrlPrefix, Integer connectTimeoutMillis, Integer readTimeoutMillis, Integer writeTimeoutMillis, Integer retryTimes) {

        // check
        Preconditions.checkArgument(!Strings.isNullOrEmpty(serviceUrlPrefix), "server host is empty!");
        Preconditions.checkArgument(connectTimeoutMillis > 0, "connect timeout mills is less than zero!");
        Preconditions.checkArgument(readTimeoutMillis > 0, "read timeout mills is less than zero!");
        Preconditions.checkArgument(writeTimeoutMillis > 0, "write timeout mills is less than zero!");
        Preconditions.checkArgument(retryTimes > 0, "retry times is less than zero!");

        this.retryTimes = retryTimes;
        this.serviceUrlPrefix = serviceUrlPrefix;
        this.connectTimeoutMillis = connectTimeoutMillis;
        this.readTimeoutMillis = readTimeoutMillis;
        this.writeTimeoutMillis = writeTimeoutMillis;

        init();
    }

    private void init() {

        // 初始化 ok http client
        okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(MonitorInterceptor.getInstance())
                .connectTimeout(this.connectTimeoutMillis, TimeUnit.MILLISECONDS)
                .readTimeout(this.readTimeoutMillis, TimeUnit.MILLISECONDS)
                .writeTimeout(this.writeTimeoutMillis, TimeUnit.MILLISECONDS)
                .build();

        if (retryTimes > 0) {
            okHttpClient = okHttpClient.newBuilder().addInterceptor(new XiaodaiSupportRetryInterceptor(retryTimes)).build();
        }

        if (!Strings.isNullOrEmpty(this.serviceUrlPrefix)) {
            // 初始化 Retrofit
            retrofit = new Retrofit.Builder()
                    .client(this.okHttpClient)
                    .baseUrl(this.serviceUrlPrefix)
                    .validateEagerly(false)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(new XiaodaiSupportDefaultRetrofitAdapterFactory())
                    .build();
        }

    }

    private void initNoRetry() {

        // 初始化 ok http client
        okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(MonitorInterceptor.getInstance())
                .connectTimeout(this.connectTimeoutMillis, TimeUnit.MILLISECONDS)
                .readTimeout(this.readTimeoutMillis, TimeUnit.MILLISECONDS)
                .writeTimeout(this.writeTimeoutMillis, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(false)
                .build();

        if (!Strings.isNullOrEmpty(this.serviceUrlPrefix)) {
            // 初始化 Retrofit
            retrofit = new Retrofit.Builder()
                    .client(this.okHttpClient)
                    .baseUrl(this.serviceUrlPrefix)
                    .validateEagerly(false)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(new XiaodaiSupportDefaultRetrofitAdapterFactory())
                    .build();
        }

    }

    /**
     * 创建Retrofit接口对象
     *
     * @param apiServiceClass apiServiceClass
     * @param <T>             apiService
     * @return apiService
     */
    public <T> T createApi(final Class<T> apiServiceClass) {
        return this.retrofit.create(apiServiceClass);
    }

    /**
     * 根据一个url和参数获得接口的String
     *
     * @param url
     * @param params
     * @param isLogInfo
     * @return
     */
    public String simpleGetBodyString(String url, Map<String, String> params, boolean isLogInfo, String reqId) {

        String bodyString = null;
        try {
            StringBuffer stringBuffer = new StringBuffer(url);
            stringBuffer.append("?");
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String value = entry.getValue();
                if (value == null) {
                    value = "";
                }
                stringBuffer.append(entry.getKey() + "=" + URLEncoder.encode(value, Charsets.UTF_8.name()) + "&");
            }
            stringBuffer.replace(stringBuffer.length() - 1, stringBuffer.length(), "");
            Request request = new Request.Builder().url(stringBuffer.toString()).header(REQ_ID, reqId).build();
            // 为了释放连接资源
            try (Response response = okHttpClient.newCall(request).execute()) {
                bodyString = response.body().string();
            }

            return bodyString;

        } catch (Exception e) {
            logger.error(MODULE_NAME + "url=" + getUrlPathWithout(url) + ", params={}", XiaodaiSupportJsonUtil.toJsonForSafetyFiled(params), e);
        } finally {
            if (isLogInfo) {
                logger.info(MODULE_NAME + "url={}, params={}, result={}",
                        url,
                        StringUtils.substring(XiaodaiSupportJsonUtil.toJsonForSafetyFiled(params), 0, 500),
                        StringUtils.substring(bodyString, 0, 500));
            }
        }
        return null;
    }

    public String executeGetWithHeaders(String url, Map<String, String> params, Map<String, String> headers) {

        String bodyString = null;
        try {
            StringBuffer stringBuffer = new StringBuffer(url);
            stringBuffer.append("?");
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String value = entry.getValue();
                if (value == null) {
                    value = "";
                }
                stringBuffer.append(entry.getKey() + "=" + URLEncoder.encode(value, Charsets.UTF_8.name()) + "&");
            }
            stringBuffer.replace(stringBuffer.length() - 1, stringBuffer.length(), "");
            Request.Builder requestBuilder = new Request.Builder().url(stringBuffer.toString());

            if (headers != null && headers.size() > 0) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    requestBuilder.addHeader(header.getKey(), header.getValue());
                }
            }

            // 为了释放连接资源
            try (Response response = okHttpClient.newCall(requestBuilder.build()).execute()) {
                ResponseBody responseBody = response.body();
                if (responseBody == null) {
                    logger.error("executeGetWithHeaders-返回空,url=" + url);
                    return null;
                }
                bodyString = responseBody.string();
            }

            return bodyString;

        } catch (Exception e) {
            logger.error(MODULE_NAME + "url=" + getUrlPathWithout(url) + ", params={}", XiaodaiSupportJsonUtil.toJsonForSafetyFiled(params), e);
        }
        return null;
    }


    /**
     * post发送
     *
     * @param oriUrl
     * @param urlParams url中?后的参数kv
     * @param bodyKv POST的Body参数
     * @param headers
     * @return
     */
    public String postWithUrlParamsBodyKvHeadersReqId(String oriUrl, Map<String, Object> urlParams, Map<String, Object> bodyKv, Map<String, String> headers, String reqId) {
        String url = oriUrl;
        String bodyString = null;
        try {
            url = createEntireUrl(oriUrl, urlParams);
            FormBody.Builder builder = new FormBody.Builder();
            if (bodyKv != null) {
                for (Map.Entry<String, Object> entry : bodyKv.entrySet()) {
                    String value;
                    if (entry.getValue() instanceof String) {
                        value = (String) entry.getValue();
                    } else {
                        value = entry.getValue().toString();
                    }
                    if (value == null) {
                        value = "";
                    }
                    builder.add(entry.getKey(), value);
                }
            }
            RequestBody formBody = builder.build();
            Request.Builder requestBuilder = new Request.Builder();
            requestBuilder.url(url).post(formBody);
            if (headers != null && headers.size() > 0) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    requestBuilder.addHeader(header.getKey(), header.getValue());
                }
            }
            requestBuilder.addHeader(REQ_ID, reqId);
            // 为了释放连接资源
            try (Response response = okHttpClient.newCall(requestBuilder.build()).execute()) {
                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    bodyString = responseBody.string();
                }
            }

            return bodyString;
        } catch (Exception e) {
            logger.error(MODULE_NAME + "url=" + getUrlPathWithout(url) + ", params={}", XiaodaiSupportJsonUtil.toJsonForSafetyFiled(urlParams), e);
        }
        return null;
    }

    private static final String NULL = "null";

    private static String createEntireUrl(String url, Map<String, Object> urlParams) throws UnsupportedEncodingException {
        StringBuffer stringBuffer = new StringBuffer(url);
        stringBuffer.append("?");
        for (Map.Entry<String, Object> entry : urlParams.entrySet()) {
            Object obj = entry.getValue();
            String value = obj.toString();
            if (obj instanceof List) {
                StringBuffer sb = new StringBuffer("[");
                for (Object o : (List) obj) {
                    sb.append("\"").append(o.toString()).append("\",");
                }
                sb.replace(sb.length() - 1, sb.length(), "");
                sb.append("]");
                value = sb.toString();
            }
            if (value == null || NULL.equalsIgnoreCase(value)) {
                value = "";
            }
            stringBuffer.append(entry.getKey() + "=" + URLEncoder.encode(value, Charsets.UTF_8.name()) + "&");
        }
        stringBuffer.replace(stringBuffer.length() - 1, stringBuffer.length(), "");
        return stringBuffer.toString();
    }

    /**
     * 根据一个url和参数获得接口的body string
     * <p>
     * FormBody Type
     *
     * @param url
     * @param params
     * @param isLogInfo
     * @return
     */
    public String simplePostFormBodyString(String url, Map<String, String> params, boolean isLogInfo, String reqId) {

        String bodyString = null;
        try {
            FormBody.Builder builder = new FormBody.Builder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String value = entry.getValue();
                if (value == null) {
                    value = "";
                }
                builder.add(entry.getKey(), value);
            }
            RequestBody formBody = builder.build();
            Request request = new Request.Builder().url(url).post(formBody).header(REQ_ID, reqId).build();

            // 为了释放连接资源
            try (Response response = okHttpClient.newCall(request).execute()) {
                bodyString = response.body().string();
            }

            return bodyString;

        } catch (Exception e) {
            logger.error(MODULE_NAME + "url=" + getUrlPathWithout(url) + ", params={}", XiaodaiSupportJsonUtil.toJsonForSafetyFiled(params), e);
        } finally {
            if (isLogInfo) {
                logger.info(MODULE_NAME + "url={}, params={}, result={}",
                        url,
                        StringUtils.substring(XiaodaiSupportJsonUtil.toJsonForSafetyFiled(params), 0, 500),
                        StringUtils.substring(bodyString, 0, 500));
            }
        }
        return null;
    }


    /**
     * post发送
     *
     * @param url
     * @param params
     * @param headers
     * @return
     */
    public String postFormBody(String url, Map<String, Object> params, Map<String, String> headers, String reqId) {
        String bodyString = null;
        try {
            FormBody.Builder builder = new FormBody.Builder();
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                String value ;
                if (entry.getValue() instanceof String) {
                    value = (String) entry.getValue();
                } else {
                    value = entry.getValue().toString();
                }
                if (value == null) {
                    value = "";
                }
                builder.add(entry.getKey(), value);
            }

            RequestBody formBody = builder.build();
            Request.Builder requestBuilder = new Request.Builder();
            requestBuilder.url(url).post(formBody);
            if (headers != null && headers.size() > 0) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    requestBuilder.addHeader(header.getKey(), header.getValue());
                }
            }
            requestBuilder.addHeader(REQ_ID, reqId);
            // 为了释放连接资源
            try (Response response = okHttpClient.newCall(requestBuilder.build()).execute()) {
                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    bodyString = responseBody.string();
                }
            }

            return bodyString;

        } catch (Exception e) {
            logger.error(MODULE_NAME + "url=" + getUrlPathWithout(url) + ", params={}", XiaodaiSupportJsonUtil.toJsonForSafetyFiled(params), e);
        }
        return null;
    }

    /**
     * 发送请求参数为对象类型
     *
     * @param url
     * @param param
     * @param headers
     * @return
     */
    public String postObjectParam(String url, Object param, Map<String, String> headers) {
        return postJsonString(url, JSONObject.toJSONString(param), headers);
    }

    /**
     * post发送一个json字符串
     *
     * @param url
     * @param jsonString
     * @param headers
     * @return
     */
    public String postJsonString(String url, String jsonString, Map<String, String> headers) {
        String bodyString = null;
        try {

            RequestBody formBody = RequestBody.create(JSON, jsonString);
            Request.Builder requestBuilder = new Request.Builder();
            requestBuilder.url(url).post(formBody);
            if (headers != null && headers.size() > 0) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    requestBuilder.addHeader(header.getKey(), header.getValue());
                }
            }
            // 为了释放连接资源
            try (Response response = okHttpClient.newCall(requestBuilder.build()).execute()) {
                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    bodyString = responseBody.string();
                }
            }

            return bodyString;

        } catch (Exception e) {
            logger.error(url + " 异常：" + e.getMessage(), e);
        }
        return null;
    }

    /**
     * 根据一个url和参数获得接口的rest api的string
     * <p>
     * FormBody Type
     *
     * @param url
     * @param params
     * @param isLogInfo
     * @return
     */
    public String simplePostJsonAndGetString(String url, Map<String, String> params, boolean isLogInfo, String reqId) {
        return this.simplePostJsonAndGetString(url, XiaodaiSupportJsonUtil.defaultGson().toJson(params), isLogInfo, reqId);
    }

    /**
     * 根据一个url和参数获得接口的rest api的string
     * <p>
     * FormBody Type
     *
     * @param url
     * @param params
     * @param isLogInfo
     * @return
     */
    public String simplePostJsonAndGetString(String url, Map<String, String> params, Map<String, String> headers, boolean isLogInfo, String reqId) {
        return this.simplePostJsonAndGetString(url, XiaodaiSupportJsonUtil.defaultGson().toJson(params), headers, isLogInfo, reqId);
    }


    /**
     * 根据一个url和参数获得接口的rest api的string
     * <p>
     * FormBody Type
     *
     * @param url
     * @param jsonString
     * @param isLogInfo
     * @return
     */
    public String simplePostJsonAndGetString(String url, String jsonString, boolean isLogInfo, String reqId) {
        return this.simplePostJsonAndGetString(url, jsonString, null, isLogInfo, reqId);
    }

    /**
     * 根据一个url和参数获得接口的rest api的string
     * <p>
     * FormBody Type
     *
     * @param url
     * @param jsonString
     * @param isLogInfo
     * @return
     */
    public String simplePostJsonAndGetString(String url, String jsonString, Map<String, String> headers, boolean isLogInfo, String reqId) {

        String bodyString = null;
        try {

            Request.Builder requestBuilder = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonString))
                    .header(REQ_ID, reqId);;

            if (headers != null && headers.size() > 0) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    requestBuilder.addHeader(header.getKey(), header.getValue());
                }
            }
            Request request = requestBuilder.build();

            // 为了释放连接资源
            try (Response response = okHttpClient.newCall(request).execute()) {
                bodyString = response.body().string();
            }

            return bodyString;

        } catch (Exception e) {
            logger.error(MODULE_NAME + "url=" + getUrlPathWithout(url) + ", params={}",jsonString ,e);
        } finally {
            if (isLogInfo) {
                logger.info(MODULE_NAME + "url={}, params={}, result={}",
                        url,
                        StringUtils.substring(jsonString, 0, 500),
                        StringUtils.substring(bodyString, 0, 500));
            }
        }
        return null;
    }

    /**
     * 根据一个url和参数获得接口的rest api的string
     * <p>
     * FormBody Type
     *
     * @param url
     * @param jsonStr
     * @return
     */
    public String simplePostJsonAndGetString(String url, String jsonStr, String reqId) {

        String bodyString = null;
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonStr))
                    .header(REQ_ID, reqId)
                    .build();

            // 为了释放连接资源
            try (Response response = okHttpClient.newCall(request).execute()) {
                bodyString = response.body().string();
            }

            return bodyString;

        } catch (Exception e) {
            logger.error(MODULE_NAME + getUrlPathWithout(url) + " 异常：" + e.getMessage());
        }
        return null;
    }

    /**
     * 根据一个url和参数获得接口的rest api的 string
     *
     * @param url
     * @param params
     * @param fileMediaType
     * @param files
     * @param isLogInfo
     * @return
     */
    public String simplePostFilesAndGetString(String url, Map<String, String> params, String fileMediaType, Map<String, byte[]> files, boolean isLogInfo, String reqId) {

        String bodyString = null;
        try {
            MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();
            multipartBodyBuilder.setType(MultipartBody.FORM);
            for (Map.Entry<String, String> param : params.entrySet()) {
                String value = param.getValue();
                if (value == null) {
                    value = "";
                }
                multipartBodyBuilder.addFormDataPart(param.getKey(), value);
            }

            for (Map.Entry<String, byte[]> file : files.entrySet()) {
                RequestBody body = RequestBody.create(MediaType.parse(fileMediaType), file.getValue());
                multipartBodyBuilder.addFormDataPart("file", file.getKey(), body);
            }

            Request request = new Request.Builder()
                    .url(url)
                    .post(multipartBodyBuilder.build())
                    .header(REQ_ID, reqId)
                    .build();

            // 为了释放连接资源
            try (Response response = okHttpClient.newCall(request).execute()) {
                bodyString = response.body().string();
            }

            return bodyString;

        } catch (Exception e) {
            logger.error(MODULE_NAME + "url=" + getUrlPathWithout(url) + ", params={}", XiaodaiSupportJsonUtil.toJsonForSafetyFiled(params), e);
        } finally {
            if (isLogInfo) {
                logger.info(MODULE_NAME + "url={}, params={}, result={}",
                        url,
                        StringUtils.substring(XiaodaiSupportJsonUtil.toJsonForSafetyFiled(params), 0, 500),
                        StringUtils.substring(bodyString, 0, 500));
            }
        }
        return null;
    }

    /**
     * 根据一个url获得接口的body byte[]二进制数据
     * <p>
     * FormBody Type
     */
    public byte[] simplePostFormBodyBytes(String url, String reqId) {
        try {
            Request request = new Request.Builder().url(url).header(REQ_ID, reqId).build();


            // 为了释放连接资源
            try (Response response = okHttpClient.newCall(request).execute()) {
                if (response.body() != null) {
                    return response.body().bytes();
                }
            }
        } catch (Exception e) {
            logger.error(MODULE_NAME + "url=" + getUrlPathWithout(url), e);
        }
        return null;
    }


    private String getUrlPathWithout(String url) {
        if (StringUtils.isEmpty(url)) {
            return url;
        }
        String[] urlSplit = url.split("\\?");
        if (urlSplit != null && urlSplit.length > 0) {
            return urlSplit[0];
        } else {
            return url;
        }
    }
}
