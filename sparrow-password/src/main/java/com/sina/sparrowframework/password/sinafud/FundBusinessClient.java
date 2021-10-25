package com.sina.sparrowframework.password.sinafud;

import com.fasterxml.jackson.databind.JsonNode;
import com.sina.sparrowframework.exception.business.BizFailException;
import com.sina.sparrowframework.metadata.constants.BaseCode;
import com.sina.sparrowframework.password.sinafud.data.FundBusinessRequest;
import com.sina.sparrowframework.password.sinafud.data.FundBusinessResponse;
import com.sina.sparrowframework.password.sinafud.util.FundKeyManager;
import com.sina.sparrowframework.password.sinafud.util.FundSignatureUtils;
import com.sina.sparrowframework.tools.utility.CipherUtils;
import com.sina.sparrowframework.tools.utility.JacksonUtil;
import com.sina.sparrowframework.tools.utility.SignatureUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 *  2021/3/12
 * @author wxn
 */
public class FundBusinessClient {

    private static final Logger logger = LoggerFactory.getLogger(FundBusinessClient.class);

    private static RestTemplate restTemplate;
    private static Environment environment;

    public  static  <T> FundBusinessResponse<T> requestObjectSinaFund(@NonNull String methodUrl
            , @NonNull FundBusinessRequest request, @Nullable Class<T> responseType) {
        logger.info("[新浪基金明文参数] methodUrl={} params={}", methodUrl, JacksonUtil.objectToJson(request));
        try {
            String requestUrl = FundKeyManager.baseUrl + methodUrl;
            request.setMerchant_key(FundKeyManager.merchantKey);
            String signStr = paramSign(request);
            Map<String,Object> params = JacksonUtil.objectToMap(request);
            params.put(FundSignatureUtils.SIGN,signStr);
            HttpEntity<Object> entity =  buildEntity(params);
            logger.info("\r\nheader:{}\r\n[新浪基金请求] url:{}\r\n请求参数:{}"
                    , entity.getHeaders(),requestUrl, entity.getBody());
            final StopWatch watch = new StopWatch("新浪基金请求接口耗时");
            watch.start();
            ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.POST,entity, String.class);
            watch.stop();
            logger.info("\r\n[新浪基金响应] url:{}\r\n响应Body:{}\r\n响应" +
                            "header:{}\r\n返回code:{}\r\n返回header:{}\r\n返回值为:{}\r\n共计执行:{}ms",
                    requestUrl, entity.getBody(), entity.getHeaders(),
                    response.getStatusCode(), response.getHeaders(), response.getBody(),
                    watch.getTotalTimeMillis());
            return convertResponse(response, responseType,methodUrl);
        } catch (Exception e) {
            logger.error("\r\n,新浪基金请求出错", e);
            throw new BizFailException(BaseCode.THIRD_REMOTE_ERROR,"新浪基金请求出错");
        }

    }

    /**
     * 字段进行密文加密(使用方自行加密)
     * @param fieldValue
     * @return
     */
    public static String fieldEncrypt(String fieldValue){
        try {
            return CipherUtils.encryptText(FundKeyManager.businessPartnerPublicKey,fieldValue);
        } catch (Exception e) {
            logger.info("新浪基金加密字段异常",e);
            throw new BizFailException(BaseCode.THIRD_REMOTE_ERROR,"新浪基金加密字段异常");
        }
    }

    /**
     * 请求参数进行签名(工具类进行签名)
     * @param requestParam
     * @return
     */
    private static String paramSign(FundBusinessRequest requestParam) {
        return FundSignatureUtils.fundSignature(JacksonUtil.objectToJson(requestParam));
    }

    /**
     * 签名校验
     */
    public static boolean verifySignature(String jsonData,String sign){
        String content = FundSignatureUtils.sortSignatureContent(jsonData);
        logger.info("verifySignature content={} jsonData={} sign={} ",content,jsonData,sign);
        if ((StringUtils.isEmpty(content) && StringUtils.isEmpty(sign))) {
            return true;
        }
        return SignatureUtils.verifySignatureWithRSA1(content,sign,FundKeyManager.businessPartnerPublicKey);
    }



    /**
     * 字段解密
     * @param fieldEncrypt
     * @return
     */
    public static String fieldDecrypt(String fieldEncrypt){
        try {
            return CipherUtils.decrypt(FundKeyManager.businessPlatformPrivateKey,fieldEncrypt);
        } catch (Exception e) {
            logger.info("新浪基金解密字段异常",e);
            throw new BizFailException(BaseCode.THIRD_REMOTE_ERROR,"新浪基金解密字段异常");
        }
    }


    private static HttpEntity<Object> buildEntity(Map<String, Object> parMap)  {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        for (Map.Entry<String, Object> entry : parMap.entrySet()) {
            param.add(entry.getKey(), entry.getValue());
        }
        HttpEntity<Object> request = new HttpEntity<>(param, httpHeaders);
        return request;
    }



    private static <T> FundBusinessResponse<T> convertResponse(
            ResponseEntity<String> entity, Class<T> clazz,String methodUrl) throws Exception {
        String body = entity.getBody();
        JsonNode root = JacksonUtil.parseTree(body);
        FundBusinessResponse response = new FundBusinessResponse();
        response.setCode(root.at(FundSignatureUtils.CODE_NODE).asText())
                .setMsg(root.at(FundSignatureUtils.MESSAGE_NODE).asText());
        if (!response.isSuccess()) {
            return response;
        }
        JsonNode node;
        if (clazz != null && root.has(FundSignatureUtils.DATA)
                && !(node = root.at(FundSignatureUtils.DATA_NODE)).isValueNode()) {
            List<String> ignores = Arrays.asList(environment.getProperty(
                    "sinafund.ignore.verify",
                    "/sdk/fundInfo").split(","));
            if (!ignores.contains(methodUrl)) {
                if(!verifySignature(node.toString(),root.at(FundSignatureUtils.SIGN_NODE).asText())) {
                    throw new BizFailException(BaseCode.THIRD_REMOTE_ERROR,"新浪基金验签失败");
                }
            }
            if (clazz.equals(String.class)) {
                response.setData(node.toString());
            }else {
                response.setData(JacksonUtil.readValue(node, clazz));

            }
        }
        return response;
    }

    public static void setRestTemplate(RestTemplate restTemplate) {
        FundBusinessClient.restTemplate = restTemplate;
    }
    public static void setEnvironment (Environment environment) {
        FundBusinessClient.environment = environment;
    }

}
