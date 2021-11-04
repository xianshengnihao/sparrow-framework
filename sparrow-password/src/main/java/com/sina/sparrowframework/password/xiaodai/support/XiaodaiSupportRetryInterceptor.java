package com.sina.sparrowframework.password.xiaodai.support;

import java.io.IOException;

import org.springframework.lang.NonNull;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 重试拦截器
 *
 * @author Ge Hui
 */
public class XiaodaiSupportRetryInterceptor implements Interceptor {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 最大重试次数
     */
    private int maxRetry;

    public XiaodaiSupportRetryInterceptor(int maxRetry) {
        this.maxRetry = maxRetry;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        Response response = null;
        int count = 0;
        IOException throwable = null;
        while (count < maxRetry) {
            try {
                // 发起网络请求
                response = chain.proceed(request);
                // 得到结果跳出循环
                break;
            } catch (IOException th) {
                count++;
                throwable = th;
            }
        }
        if (response == null) {
            final String addr = chain.request().url().host() + ":" + chain.request().url().port();
            final String path = chain.request().url().encodedPath();
            final String method = chain.request().method();
            logger.error(String.format("网络请求失败. addr=%s, path=%s, method=%s, retry=%s", addr, path, method, count), throwable);
            throw throwable;
        }
        return response;
    }
}
