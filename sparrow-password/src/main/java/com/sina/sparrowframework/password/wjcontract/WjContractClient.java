package com.sina.sparrowframework.password.wjcontract;

import com.fasterxml.jackson.databind.JsonNode;
import com.sina.sparrowframework.exception.business.BizFailException;
import com.sina.sparrowframework.metadata.constants.BaseCode;
import com.sina.sparrowframework.password.wjcontract.data.WjContractRequest;
import com.sina.sparrowframework.password.wjcontract.data.WjContractResponse;
import com.sina.sparrowframework.password.wjcontract.util.WjContractManager;
import com.sina.sparrowframework.password.wjmiddle.util.MiddleSignatureUtils;
import com.sina.sparrowframework.tools.utility.DigestUtils;
import com.sina.sparrowframework.tools.utility.JacksonUtil;
import com.sina.sparrowframework.tools.utility.Sha256Util;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * 微聚合同
 *
 * @Author liuyi
 * @Description
 * @Date 2021/11/8 14:20
 **/
public class WjContractClient {

    private static final Logger logger = LoggerFactory.getLogger(WjContractClient.class);

    private static RestTemplate restTemplate;

    private static final String PARAM_APPID = "x-wejoydata-appid";
    private static final String PARAM_TIMESTAMP = "x-wejoydata-timestamp";
    private static final String PARAM_SIGNATURE = "x-wejoydata-signature";
    private static final String PARAM_BRANDNAME = "brandName";
    private static final String PARAM_BODYMD5 = "bodyMd5";
    private static final String PARAM_UID = "uid";
    private static final String PARAM_USER_AGENT = "User-Agent";

    public static final String CONTROL_NODE = "/control";
    public static final String DATA_NODE = "/data";


    public static <T> WjContractResponse<T> requestWjContract(@NonNull String methodUrl
            , @NonNull WjContractRequest request, @Nullable Class<T> responseType) {
        logger.info("[微聚合同明文参数] methodUrl={} params={}", methodUrl, JacksonUtil.objectToJson(request));
        try {
            String requestUrl = WjContractManager.baseUrl + methodUrl;
            String signStr = generateSign(request);
            HttpEntity<Object> entity = buildEntity(request, signStr);
            logger.info("\r\nheader:{}\r\n[微聚合同请求] url:{}\r\n请求参数:{}", entity.getHeaders(), requestUrl, entity.getBody());
            final StopWatch watch = new StopWatch("微聚合同请求接口耗时");
            watch.start();
            ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.POST, entity, String.class);
            watch.stop();
            logger.info("\r\n[微聚合同响应] url:{}\r\n响应Body:{}\r\n响应" +
                            "header:{}\r\n返回code:{}\r\n返回header:{}\r\n返回值为:{}\r\n共计执行:{}ms",
                    requestUrl, entity.getBody(), entity.getHeaders(),
                    response.getStatusCode(), response.getHeaders(), response.getBody(),
                    watch.getTotalTimeMillis());

            WjContractResponse wjContractResponse = JacksonUtil.jsonToObject(response.getBody(), WjContractResponse.class);
            JsonNode root = JacksonUtil.parseTree(response.getBody());
            if (wjContractResponse.getData() == null) {
                return wjContractResponse;
            }
            if (responseType.equals(String.class)) {
                root = root.at(DATA_NODE);
                wjContractResponse.setData(root.toString());
            } else {
                root = root.at(DATA_NODE);
                wjContractResponse.setData(JacksonUtil.readValue(root, responseType));
            }

            return wjContractResponse;
        } catch (Exception e) {
            logger.error("\r\n,微聚合同请求出错", e);
            throw new BizFailException(BaseCode.THIRD_REMOTE_ERROR, "微聚合同请求出错");
        }

    }

    private static HttpEntity<Object> buildEntity(WjContractRequest request, String signStr) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.add(PARAM_UID, request.getUid());
        httpHeaders.add(PARAM_APPID, WjContractManager.appid);
        httpHeaders.add(PARAM_TIMESTAMP, String.valueOf(request.getTimestamp()));
        httpHeaders.add(PARAM_BRANDNAME, WjContractManager.brandName);
        httpHeaders.add(PARAM_SIGNATURE, signStr);
        httpHeaders.add(PARAM_USER_AGENT, WjContractManager.appid);
        return new HttpEntity<>(JacksonUtil.objectToJson(request.getBody()), httpHeaders);
    }

    private static String generateSign(WjContractRequest request) throws Exception {
        String bodyMd5 = null;
        if (request.getBody() != null) {
            bodyMd5 = DigestUtils.md5Hex(JacksonUtil.objectToJson(request.getBody()));
        }
        Map<String, Object> mapSignData = new HashMap<>();
        mapSignData.put(PARAM_BODYMD5, bodyMd5);
        mapSignData.put(PARAM_TIMESTAMP, String.valueOf(request.getTimestamp()));
        mapSignData.put(PARAM_APPID, WjContractManager.appid);
        String plaintext = getRequestFormat(mapSignData);
        try {
            return Sha256Util.encryptByMacSha256(plaintext, WjContractManager.appSecret);
        } catch (Exception e) {
            throw e;
        }
    }


    public static String getRequestFormat(Map<String, Object> map) {
        Map<String, Object> resultMap = new TreeMap<>();
        for (Map.Entry entry : map.entrySet()) {
            String key = (String) entry.getKey();
            Object value = entry.getValue();
            if (StringUtils.isEmpty(key) || value == null) {
                continue;
            }
            resultMap.put(key, value);
        }
        StringBuilder buff = new StringBuilder();
        for (Map.Entry<String, Object> entry : resultMap.entrySet()) {
            buff.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        String buffStr = buff.toString();
        if (StringUtils.isNotEmpty(buffStr)) {
            buffStr = buffStr.substring(0, buffStr.length() - 1);
        }
        return buffStr;
    }


    public static void setRestTemplate(RestTemplate restTemplate) {
        WjContractClient.restTemplate = restTemplate;
    }

}
