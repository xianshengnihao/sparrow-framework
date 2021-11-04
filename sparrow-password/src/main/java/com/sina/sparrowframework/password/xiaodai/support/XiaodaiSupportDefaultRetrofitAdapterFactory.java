package com.sina.sparrowframework.password.xiaodai.support;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;

/**
 * RetrofitAdapterFactory
 * 将Retrofit接口到Call对象转换为Response对象
 *
 * @author liuchang15
 * @date 2019-10-11
 */
public class XiaodaiSupportDefaultRetrofitAdapterFactory extends CallAdapter.Factory {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        final Type responseType = getResponseType(returnType);

        return new CallAdapter<Object, Object>() {

            @Override
            public Type responseType() {
                return responseType;
            }

            @Override
            public Object adapt(Call<Object> call) {
                long startTimeMillis = System.currentTimeMillis();
                Object rtn = null;
                String requestPath = call.request().url().encodedPath();
                String host = call.request().url().host();
                try {
                    rtn = call.execute().body();

                } catch (IOException ex) {
                    throw new RuntimeException(ex);

                } catch (Throwable th) {
                    logger.error("接口调用 api serverUrl=" + host + "serviceName=" + requestPath + " error=" + th.getMessage(), th);
                    throw th;

                } finally {
                    long latencyMills = System.currentTimeMillis() - startTimeMillis;
                    logger.info("接口调用 api method={} response={} cost={} ms", requestPath, XiaodaiSupportJsonUtil.toJsonForSafetyFiled(rtn), latencyMills);
                }
                return rtn;
            }
        };
    }

    private Type getResponseType(Type type) {
        if (type instanceof WildcardType) {
            return ((WildcardType) type).getUpperBounds()[0];
        }
        return type;
    }
}