package com.sina.sparrowframework.password.xiaodai.support;

import java.util.Map;

/**
 * 由于gson在将json反序列化为Object时对Numberic默认序列化为double类型，可能会出现不符合预期的转型异常。
 * 因此废弃所有返回类型为Map<String, Object>类型的方法。
 *
 * 对返回的json串处理由调用方依照http接口定义自行处理。
 * ps.建议定义接口返回类型bean,使用
 * T XiaodaiSupportJsonUtil.defaultGson().fromJson(String json, Class<T> clz);
 * 直接序列化为返回对象。
 * 公共的简易Http请求的封装
 *
 * @author Ge Hui
 */
public class XiaodaiSupportBaseHttpClientUtils {

    private static final XiaodaiSupportBaseHttpClient BASE_HTTP_CLIENT = new XiaodaiSupportBaseHttpClient();

    private static XiaodaiSupportBaseHttpClient BASE_HTTP_CLIENT_MINUTE = null;
    private static XiaodaiSupportBaseHttpClient BASE_HTTP_CLIENT_3_SEC = null;
    private static XiaodaiSupportBaseHttpClient BASE_HTTP_CLIENT_30_SEC = null;
    private static XiaodaiSupportBaseHttpClient BASE_HTTP_CLIENT_10_SEC = null;

    public static XiaodaiSupportBaseHttpClient getBaseHttpClient3Sec() {
        if (BASE_HTTP_CLIENT_3_SEC == null) {
            synchronized (XiaodaiSupportBaseHttpClientUtils.class) {
                if (BASE_HTTP_CLIENT_3_SEC == null) {
                    BASE_HTTP_CLIENT_3_SEC = new XiaodaiSupportBaseHttpClient(3000, 3000, 3000);
                }
            }
        }
        return BASE_HTTP_CLIENT_3_SEC;
    }

    public static XiaodaiSupportBaseHttpClient getBaseHttpClient10Sec() {
        if (BASE_HTTP_CLIENT_10_SEC == null) {
            synchronized (XiaodaiSupportBaseHttpClientUtils.class) {
                if (BASE_HTTP_CLIENT_10_SEC == null) {
                    BASE_HTTP_CLIENT_10_SEC = new XiaodaiSupportBaseHttpClient(3000, 10000, 10000);
                }
            }
        }
        return BASE_HTTP_CLIENT_10_SEC;
    }

    public static XiaodaiSupportBaseHttpClient getBaseHttpClient30Sec() {
        if (BASE_HTTP_CLIENT_30_SEC == null) {
            synchronized (XiaodaiSupportBaseHttpClientUtils.class) {
                if (BASE_HTTP_CLIENT_30_SEC == null) {
                    BASE_HTTP_CLIENT_30_SEC = new XiaodaiSupportBaseHttpClient(3000, 30000, 30000);
                }
            }
        }
        return BASE_HTTP_CLIENT_30_SEC;
    }

    public static XiaodaiSupportBaseHttpClient getBaseHttpClientMinute() {
        if (BASE_HTTP_CLIENT_MINUTE == null) {
            synchronized (XiaodaiSupportBaseHttpClientUtils.class) {
                if (BASE_HTTP_CLIENT_MINUTE == null) {
                    BASE_HTTP_CLIENT_MINUTE = new XiaodaiSupportBaseHttpClient(3000, 60000, 60000);
                }
            }
        }
        return BASE_HTTP_CLIENT_MINUTE;
    }

    /**
     * 根据一个url和参数获得接口的String
     *
     * @param url
     * @param params
     * @return
     */
    public static String simpleGetBodyString(String url, Map<String, String> params, String reqId) {
        return BASE_HTTP_CLIENT.simpleGetBodyString(url, params, true, reqId);
    }

    /**
     * 根据一个url和参数获得接口的String
     *
     * @param url
     * @param params
     * @param isLogInfo
     * @return
     */
    public static String simpleGetBodyString(String url, Map<String, String> params, boolean isLogInfo, String reqId) {
        return BASE_HTTP_CLIENT.simpleGetBodyString(url, params, isLogInfo, reqId);
    }

    /**
     * 根据一个url和参数获得接口的body string
     * <p>
     * FormBody Type
     *
     * @param url
     * @param params
     * @return
     */
    public static String simplePostFormBodyString(String url, Map<String, String> params, String reqId) {
        return BASE_HTTP_CLIENT.simplePostFormBodyString(url, params, true, reqId);
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
    public static String simplePostFormBodyString(String url, Map<String, String> params, boolean isLogInfo, String reqId) {
        return BASE_HTTP_CLIENT.simplePostFormBodyString(url, params, isLogInfo, reqId);
    }

    /**
     * 根据一个url和参数获得接口的rest api的string
     * <p>
     * FormBody Type
     *
     * @param url
     * @param params
     * @return
     */
    public static String simplePostJsonAndGetString(String url, Map<String, String> params, String reqId) {
        return BASE_HTTP_CLIENT.simplePostJsonAndGetString(url, params, true, reqId);
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
    public static String simplePostJsonAndGetString(String url, Map<String, String> params, boolean isLogInfo, String reqId) {
        return BASE_HTTP_CLIENT.simplePostJsonAndGetString(url, params, isLogInfo, reqId);
    }

    /**
     * 根据一个url和参数获得接口的rest api的string
     * <p>
     * FormBody Type
     *
     * @param url
     * @param params
     * @param baseHttpClient
     * @return
     */
    public static String simplePostJsonAndGetString(String url, Map<String, String> params, XiaodaiSupportBaseHttpClient baseHttpClient, String reqId) {
        return baseHttpClient.simplePostJsonAndGetString(url, params, true, reqId);
    }

    /**
     * 根据一个url和参数获得接口的rest api的string
     * <p>
     * FormBody Type
     *
     * @param url
     * @param json
     * @param baseHttpClient
     * @return
     */
    public static String simplePostJsonAndGetString(String url, String json, XiaodaiSupportBaseHttpClient baseHttpClient, String reqId) {
        return baseHttpClient.simplePostJsonAndGetString(url, json, true, reqId);
    }

    /**
     * 指定参数和header的post方法
     * @param url
     * @param params
     * @param headers
     * @param baseHttpClient
     * @return
     */
    public static String postFormBody(String url, Map<String, Object> params, Map<String, String> headers, XiaodaiSupportBaseHttpClient baseHttpClient, String reqId) {
        return baseHttpClient.postFormBody(url, params, headers, reqId);
    }

    /**
     * 根据一个url和参数获得接口的rest api的 string
     *
     * @param url
     * @param params
     * @param fileMediaType
     * @param files
     * @return
     */
    public static String simplePostFilesAndGetString(String url, Map<String, String> params, String fileMediaType, Map<String, byte[]> files, String reqId) {
        return BASE_HTTP_CLIENT.simplePostFilesAndGetString(url, params, fileMediaType, files, true, reqId);
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
    public static String simplePostFilesAndGetString(String url, Map<String, String> params, String fileMediaType, Map<String, byte[]> files, boolean isLogInfo, String reqId) {
        return BASE_HTTP_CLIENT.simplePostFilesAndGetString(url, params, fileMediaType, files, isLogInfo, reqId);
    }

    /**
     * 根据一个url获得接口的body byte[]二进制数据
     * <p>
     * FormBody Type
     */
    public static byte[] simplePostFormBodyBytes(String url, String reqId) {
        return BASE_HTTP_CLIENT.simplePostFormBodyBytes(url, reqId);
    }


}
