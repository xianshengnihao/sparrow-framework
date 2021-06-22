package com.sina.sparrowframework.password.wjmiddle;


import com.sina.sparrowframework.password.wjmiddle.data.MiddleServiceRequest;
import com.sina.sparrowframework.password.wjmiddle.data.MiddleServiceResponse;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 *微聚中台调用服务
 * @author wxn
 */
public class WjMiddleServiceClient {

    public  static  <T> MiddleServiceResponse<T> requestWjMiddleService(@NonNull String requestUrl
            , @NonNull MiddleServiceRequest request, @Nullable Class<T> responseType) {

        return null;
    }
}
