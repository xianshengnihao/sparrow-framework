package com.sina.sparrowframework.password.wjmiddle;

import com.sina.sparrowframework.password.wjmiddle.data.MiddleServiceRequest;
import com.sina.sparrowframework.password.wjmiddle.data.MiddleServiceResponse;
import com.sina.sparrowframework.password.wjmiddle.util.MiddleKeyManager;
import com.sina.sparrowframework.password.wjmiddle.util.MiddleSignatureUtils;
import com.sina.sparrowframework.tools.utility.JacksonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 *微聚中台调用服务
 * @author wxn
 */
public class WjMiddleServiceClient {
    private static final Logger logger = LoggerFactory.getLogger(WjMiddleServiceClient.class);

    private static RestTemplate restPasswordTemplate;
    private static Environment environment;

    /**
     * 调用中台加解密通用接口
     * @param requestUrl 请求方法URL
     * @param request 请求参数
     * @param responseType 响应数据类型
     * @param <T>
     * @return 响应结果数据
     * @throws Exception 业务处理异常
     */
    public  static  <T> MiddleServiceResponse<T> requestWjMiddleService(@NonNull String requestUrl
            , @NonNull MiddleServiceRequest request, @Nullable Class<T> responseType) throws Exception {
        MiddleServiceResponse middle = new MiddleServiceResponse();
        Map paramMap = JacksonUtil.objectToMap(request);
        paramMap.put("business", MiddleKeyManager.business);
        paramMap.put("timestamp", String.valueOf((System.currentTimeMillis() / 1000)));
        String sign = MiddleSignatureUtils.middleSignature(JacksonUtil.objectToJson(paramMap));
        paramMap.put("sign", sign);
        HttpEntity<Object> entity = buildEntityData(paramMap);
        final StopWatch watch = new StopWatch("微聚中台请求接口耗时");
        watch.start();
        logger.info("\r\nheader:{}\r\n[微聚中台请求]url:{}\r\n请求参数:{}",
                 entity.getHeaders()
                ,requestUrl, entity.getBody());
        ResponseEntity<String> response =
                restPasswordTemplate.
                        exchange(MiddleKeyManager.baseUrl+requestUrl
                                , HttpMethod.POST
                                ,entity
                                , String.class);
        watch.stop();
        logger.info("\r\n[微聚中台响应] url:{}\r\n响应Body:{}\r\n响应" +
                        "header:{}\r\n返回code:{}\r\n共计执行:{}ms",
                requestUrl, response.getBody(), response.getHeaders(),
                response.getStatusCode(),
                watch.getTotalTimeMillis());
        MiddleServiceResponse<String> middleServiceResponse
                = JacksonUtil.jsonToObject(response.getBody(),MiddleServiceResponse.class);
        middle.setCode(middleServiceResponse.getCode());
        middle.setMsg(middleServiceResponse.getMsg());
        if (!middle.isSuccess()) {
            return middle;
        }
        if (responseType.equals(String.class)) {
            middle.setData(middleServiceResponse.getData());
        }else {
            middle.setData(JacksonUtil.jsonToObject(middleServiceResponse.getData(),responseType));
        }
        return middle;
    }

    private static HttpEntity<Object> buildEntityData(Map<String, Object> parMap)  {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        for (Map.Entry<String, Object> entry : parMap.entrySet()) {
            param.add(entry.getKey(), entry.getValue());
        }
        HttpEntity<Object> request = new HttpEntity<>(param, httpHeaders);
        return request;
    }

    public static void setRestTemplate(RestTemplate restPasswordTemplate) {
        WjMiddleServiceClient.restPasswordTemplate = restPasswordTemplate;
    }
    public static void setEnvironment (Environment environment) {
        WjMiddleServiceClient.environment = environment;
    }
}
