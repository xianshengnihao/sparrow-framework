package com.sina.sparrowframework.password.wjdata;

import com.fasterxml.jackson.databind.JsonNode;
import com.sina.sparrowframework.exception.business.BizFailException;
import com.sina.sparrowframework.metadata.constants.BaseCode;
import com.sina.sparrowframework.password.wjdata.data.WjDataABTestRequest;
import com.sina.sparrowframework.password.wjdata.data.WjDataBaseResponse;
import com.sina.sparrowframework.password.wjdata.util.WjDataKeyManager;
import com.sina.sparrowframework.tools.utility.JacksonUtil;
import com.sina.sparrowframework.tools.utility.UUIDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import static com.sina.sparrowframework.password.wjdata.util.WjDataKeyManager.PARAM_REFERER;
import static com.sina.sparrowframework.password.wjdata.util.WjDataKeyManager.PARAM_USER_AGENT;


/**
 * 发起aBTest实验
 */
public class ABTestServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(ABTestServiceClient.class);

    private static RestTemplate restTemplate;

    private static Environment environment;

    /**
     * 发起aBTest实验
     */
    public  static  <T> WjDataBaseResponse<T> abTest(@NonNull WjDataABTestRequest request,@Nullable Class<T> responseType) {
        logger.info("[发起aBTest实验明文参数] params={}", JacksonUtil.objectToJson(request));
        try {
            String requestUrl = WjDataKeyManager.baseUrl + environment.getRequiredProperty("wjData.aBTest.methodUrl");
            final StopWatch watch = new StopWatch("aBTest实验请求接口耗时");
            watch.start();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add(PARAM_USER_AGENT, "licai");
            request.setChannel("licai").setApp("web");
            ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.POST,new HttpEntity<>(JacksonUtil.objectToJson(request), headers), String.class);
            watch.stop();
            logger.info("\r\n[发起aBTest实验响应] 返回code:{}\r\n返回header:{}\r\n返回值为:{}\r\n共计执行:{}ms",
                    response.getStatusCode(), response.getHeaders(), response.getBody(),
                    watch.getTotalTimeMillis());
            return convertResponse(response, responseType);
        } catch (Exception e) {
            logger.error("\r\n发起aBTest实验出错", e);
            throw new BizFailException(BaseCode.THIRD_REMOTE_ERROR,"发起aBTest实验出错");
        }
    }

    private static <T> WjDataBaseResponse convertResponse(
            ResponseEntity<String> entity, Class<T> clazz) throws Exception {
        JsonNode root = JacksonUtil.parseTree(entity.getBody());
        WjDataBaseResponse response = new WjDataBaseResponse();
        response.setCode(root.at("/code").asText())
                .setMsg(root.at("/msg").asText());
        if (!response.isSuccess()) {
            return response;
        }
        JsonNode node;
        if (clazz != null && root.has("data")
                && !(node = root.at("/data")).isValueNode()) {
            if (clazz.equals(String.class)) {
                response.setData(node.toString());
            }else {
                response.setData(JacksonUtil.readValue(node, clazz));
            }
        }
        return response;
    }

    public static void setRestTemplate(RestTemplate restTemplate) {
        ABTestServiceClient.restTemplate = restTemplate;
    }

    public static void setEnvironment (Environment environment) {
        ABTestServiceClient.environment = environment;
    }
}
