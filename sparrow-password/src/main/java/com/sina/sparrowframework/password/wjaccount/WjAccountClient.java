package com.sina.sparrowframework.password.wjaccount;


import com.sina.sparrowframework.exception.business.BizFailException;
import com.sina.sparrowframework.metadata.constants.BaseCode;
import com.sina.sparrowframework.password.wjaccount.data.FinanceBusinessRequest;
import com.sina.sparrowframework.password.wjaccount.data.FinanceBusinessResponse;
import com.sina.sparrowframework.password.wjaccount.data.FinancePublicToken;
import com.sina.sparrowframework.password.wjaccount.data.FinanceUserInfo;
import com.sina.sparrowframework.password.wjaccount.utils.WjAccountKeyManager;
import com.sina.sparrowframework.password.wjcontract.util.WjContractManager;
import com.sina.sparrowframework.tools.utility.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.*;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.sina.sparrowframework.password.wjaccount.utils.WjAccountSignatureUtils.*;
import static com.sina.sparrowframework.password.wjaccount.utils.WjAccountKeyManager.*;

/**
 *微聚金融账户调用服务
 * @author wxn
 */
public class WjAccountClient {
    private static final Logger logger = LoggerFactory.getLogger(WjAccountClient.class);

    private static RestTemplate restTemplate;

    private static StringRedisTemplate stringRedisTemplate;

    public  static  <T> FinanceBusinessResponse<T> requestObjectSinaFinance(@NonNull String requestUrl
            , @NonNull FinanceBusinessRequest request, @Nullable Class<T> responseType) {
        try {
            FinancePublicToken publicToken = getTokenData();
            String userToken = getUserToken(publicToken,request.getWbId());
            String paramsEncrypt = CipherUtils.encryptByAesEcbPkcs5padding(
                    JacksonUtil.objectToJson(request),publicToken.getPublicAesKey());
            Map<String, Object> parMap = new HashMap<>();
            parMap.put(DATA,paramsEncrypt);

            final StopWatch watch = new StopWatch("金融账户请求接口耗时");
            watch.start();
            HttpEntity<Object> entity = buildEntityData(publicToken,userToken,parMap);
            logger.info("\r\n微博ID:{}\r\nheader:{}\r\n[金融账户请求]url:{}\r\n明文参数:{}\r\n请求参数:{}",
                    request.getWbId(), entity.getHeaders()
                    ,requestUrl,JacksonUtil.objectToJson(request), entity.getBody());
            ResponseEntity<String> response =
                    restTemplate.
                            exchange(baseUrl+requestUrl
                                    , HttpMethod.POST
                                    ,entity
                                    , String.class);
            watch.stop();
            logger.info("\r\n[金融账户响应] url:{}\r\n响应Body:{}\r\n响应" +
                            "header:{}\r\n返回code:{}\r\n返回header:{}\r\n共计执行:{}ms",
                    requestUrl, response.getBody(), response.getHeaders(),
                    response.getStatusCode(), response.getHeaders(),
                    watch.getTotalTimeMillis());

            FinanceBusinessResponse<String>  financeDataRes
                    = JacksonUtil.jsonToObject(response.getBody(),FinanceBusinessResponse.class);
            FinanceBusinessResponse returnRes = new FinanceBusinessResponse();
            returnRes.setControl(financeDataRes.getControl());
            String aesData = StringUtils.isEmpty(financeDataRes.getData())
                    ? "": CipherUtils.decryptByAesEcbPkcs5padding(financeDataRes.getData()
                    , publicToken.getPublicAesKey());
            logger.info("\r\n[金融账户解密数据] url:{}\r\n响应Body:{}\r\n返回值为:{}",
                    requestUrl, response.getBody(), aesData
            );
            if (!financeDataRes.isSuccess()) {
                return returnRes;
            }

            if (responseType.equals(String.class)) {
                returnRes.setData(aesData);
            }else {
                returnRes.setData(JacksonUtil.jsonToObject(aesData,responseType));
            }
            return returnRes;
        } catch (Exception e) {
            logger.error("\r\n,金融账户请求出错", e);
            Map contentMap = new HashMap();
            contentMap.put("url", requestUrl);
            contentMap.put("req", request);
            throw new BizFailException(BaseCode.THIRD_REMOTE_ERROR,"金融账户异常");
        }
    }

    /**
     * 获取有效的商户token
     * @return
     */
    public static FinancePublicToken getTokenData(){
        if (stringRedisTemplate.hasKey(FINANCE_X_MERCHANT_TOKEN_REDIS_KEY)) {
            String value = stringRedisTemplate.opsForValue().get(FINANCE_X_MERCHANT_TOKEN_REDIS_KEY);
            return JacksonUtil.jsonToObject(value, FinancePublicToken.class);
        }
        String publicToken = getOriginPublicToken();
        String publicAes = getOriginPublicAes(publicToken);
        validCipherKey(publicAes,publicToken);
        FinancePublicToken tokenData = new FinancePublicToken();
        tokenData.setPublicAesKey(publicAes);
        tokenData.setPublicToken(publicToken);
        stringRedisTemplate.opsForValue().set(FINANCE_X_MERCHANT_TOKEN_REDIS_KEY
                ,JacksonUtil.objectToJson(tokenData),getExpTokenTime(publicToken), TimeUnit.SECONDS);
        return tokenData;
    }

    public static String getUserToken(FinancePublicToken publicToken,String wbId){
        try {
            if (stringRedisTemplate.hasKey(String.format(FINANCE_USER_TOKEN_REDIS_KEY,wbId))) {
                return stringRedisTemplate.opsForValue().get(String.format(FINANCE_USER_TOKEN_REDIS_KEY,wbId));
            }
            Map<String, Object> loginMap = new HashMap<>();
            loginMap.put(thirdIdName,wbId);
            loginMap.put(loginTypeName,"third");
            String loginMapLastStr = CipherUtils.encryptByAesEcbPkcs5padding(
                    JacksonUtil.objectToJson(loginMap),publicToken.getPublicAesKey());
            Map<String, Object> loginMapLast = new HashMap<>();
            loginMapLast.put(DATA,loginMapLastStr);
            ResponseEntity<String> loginRes =
                    restTemplate.
                            exchange(baseUrl+ loginUrl
                                    , HttpMethod.POST,buildEntityToken(publicToken.getPublicToken(),loginMapLast)
                                    , String.class);
            FinanceBusinessResponse<String> passportTokenRes
                    = JacksonUtil.jsonToObject(loginRes.getBody(),FinanceBusinessResponse.class);
            if (!passportTokenRes.isSuccess()) {
                logger.error("金融账户用户登录数据异常 wbId= {} res={}",wbId,loginRes.getBody());
                throw new BizFailException(BaseCode.THIRD_REMOTE_ERROR,passportTokenRes.getControl().getMessage());
            }
            String passportTokenAesData = CipherUtils.decryptByAesEcbPkcs5padding(
                    passportTokenRes.getData(),publicToken.getPublicAesKey());
            Map<String,String> passportTokenMap = JacksonUtil.jsonToMap(passportTokenAesData);
            String xPassportToken = passportTokenMap.get(xPassportTokenName);
            //添加缓存用户登录token
            stringRedisTemplate.opsForValue().set(String.format(FINANCE_USER_TOKEN_REDIS_KEY,wbId)
                    ,xPassportToken,getExpTokenTime(xPassportToken),TimeUnit.SECONDS);
            return xPassportToken;
        } catch (Exception e) {
            logger.error("金融账户wbId={}获取用户token异常",wbId,e);
            throw new BizFailException(BaseCode.THIRD_REMOTE_ERROR,"金融账户获取用户token异常");
        }
    }

    /**
     * 删除用户token
     * @param wbId
     */
    public static void refreshUserToken(String wbId){
        logger.info("删除金融账户token wbId={}",wbId);
        stringRedisTemplate.delete(String.format(FINANCE_USER_TOKEN_REDIS_KEY,wbId));
    }

    public static FinanceUserInfo getFinanceUserInfo(String wbId){
        FinancePublicToken publicToken = getTokenData();
        String userToken = getUserToken(publicToken,wbId);
        Map<String,String> decodeMap =
                JacksonUtil.jsonToMap(
                        new String(Base64.getDecoder().decode(userToken.split("\\.")[1])
                                , StandardCharsets.UTF_8));
        return new FinanceUserInfo()
                .setLgId(decodeMap.get("lgid"))
                .setUid(decodeMap.get("uid"))
                .setPid(decodeMap.get("pid"))
                .setUserToken(userToken);
    }

    /**
     * 调用金融账户查询商户token
     * @return
     */
    private static String getOriginPublicToken(){
        try {
            Map<String, Object> parMap = new HashMap<>();
            parMap.put(appIdName,appId);
            String encAppKey = CipherUtils.encryptWithRsaPublic(financePartnerPublicKeyStr, CipherUtils.Algorithm.RSA,appKey);
            parMap.put(appKeyName,encAppKey);
            ResponseEntity<String> responseToken =
                    restTemplate.
                            exchange(baseUrl+getTokenUrl
                                    , HttpMethod.POST,buildEntity(parMap)
                                    , String.class);
            FinanceBusinessResponse<String> tokenRes
                    = JacksonUtil.jsonToObject(responseToken.getBody(),FinanceBusinessResponse.class);
            if (!tokenRes.isSuccess()) {
                logger.error("金融账户获取商户token返回结果错误 res={}",responseToken.getBody());
                throw new BizFailException(BaseCode.THIRD_REMOTE_ERROR,tokenRes.getControl().getMessage());
            }
            Map<String,String> tokenMap = JacksonUtil.jsonToMap(tokenRes.getData());
            String token = tokenMap.get(tokenName);
            token =  CipherUtils.decryptWithRsaPublic128(financePartnerPublicKeyStr, CipherUtils.Algorithm.RSA,token);
            return token;
        } catch (Exception e) {
            logger.error("金融账户获取商户token异常",e);
            throw new BizFailException(BaseCode.THIRD_REMOTE_ERROR,"获取商户token异常");
        }
    }

    /**
     * 获取AES秘钥
     * @param publicToken
     * @return
     */
    private static String getOriginPublicAes(String publicToken){
        try {
            String preMasterKey = RandomStringUtil.getRandomString(8, true);
            String preMasterKeyEn= CipherUtils.encryptWithRsaPublic( financePartnerPublicKeyStr
                    , CipherUtils.Algorithm.RSA,preMasterKey);
            Map<String, Object> parMapPreMasterKey = new HashMap<>();
            parMapPreMasterKey.put(preMasterKeyName,preMasterKeyEn);
            parMapPreMasterKey.put(cipherSuiteName,100);

            ResponseEntity<String> responseCipher = restTemplate.
                    exchange(baseUrl+getCipherKeyUrl
                            , HttpMethod.POST,buildEntityToken(publicToken,parMapPreMasterKey)
                            , String.class);
            FinanceBusinessResponse<String> backMasterKeyRes
                    = JacksonUtil.jsonToObject(responseCipher.getBody(),FinanceBusinessResponse.class);
            if (!backMasterKeyRes.isSuccess()) {
                logger.error("秘钥协商数据异常 res={}",responseCipher.getBody());
                throw new BizFailException(BaseCode.THIRD_REMOTE_ERROR,backMasterKeyRes.getControl().getMessage());
            }
            Map<String,String> backMasterKeyMap = JacksonUtil.jsonToMap(backMasterKeyRes.getData());
            String backMasterKey = backMasterKeyMap.get(backMasterKeyName);
            backMasterKey =  CipherUtils.decryptWithRsaPublic128(financePartnerPublicKeyStr
                    ,CipherUtils.Algorithm.RSA,backMasterKey);
            String publicAes = preMasterKey + backMasterKey;
            return publicAes;
        } catch (Exception e) {
            logger.error("秘钥协商接口异常",e);
            throw new BizFailException(BaseCode.THIRD_REMOTE_ERROR,"秘钥协商接口异常");
        }

    }

    private static void validCipherKey(String publicAes,String publicToken){
        Map<String, Object> validCipherKeyMap = new HashMap<>();
        String signature = Sha256Util.getSHA256(publicAes);
        validCipherKeyMap.put(validCipherKeyName,signature);
        ResponseEntity<String> validCipherKeyRes =
                restTemplate.
                        exchange(baseUrl+validCipherKeyUrl
                                , HttpMethod.POST,buildEntityToken(publicToken,validCipherKeyMap)
                                , String.class);
        FinanceBusinessResponse<String> backMasterKeyRes
                = JacksonUtil.jsonToObject(validCipherKeyRes.getBody(),FinanceBusinessResponse.class);
        if (!backMasterKeyRes.isSuccess()) {
            logger.error("密钥验证数据异常 res={}",validCipherKeyRes.getBody());
            throw new BizFailException(BaseCode.THIRD_REMOTE_ERROR,backMasterKeyRes.getControl().getMessage());
        }
    }

    private static HttpEntity<Object> buildEntity(Map<String, Object> parMap)  {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.add(PARAM_USER_AGENT, WjAccountKeyManager.appId);
        HttpEntity<Object> request = new HttpEntity<>(parMap, httpHeaders);
        return request;
    }

    private static HttpEntity<Object> buildEntityToken(String token,Map<String, Object> parMap)  {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.add(xMerchantToken,token);
        httpHeaders.add(PARAM_USER_AGENT, WjAccountKeyManager.appId);
        HttpEntity<Object> request = new HttpEntity<>(parMap, httpHeaders);
        return request;
    }
    private static HttpEntity<Object> buildEntityData(
            FinancePublicToken publicToken
            ,String userToken
            ,Map<String, Object> parMap)  {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.add(xMerchantToken,publicToken.getPublicToken());
        httpHeaders.add(xPassportToken,userToken);
        httpHeaders.add(xPassportTimestamp, String.valueOf(DateUtil.currentTimestampSecond()+5*60L));
        HttpEntity<Object> request = new HttpEntity<>(parMap, httpHeaders);
        return request;
    }

    private static Long  getExpTokenTime(String publicToken){
        Map<String,String> decodeMap =
                JacksonUtil.jsonToMap(
                        new String(Base64.getDecoder().decode(publicToken.split("\\.")[1])
                                , StandardCharsets.UTF_8));
        Long exp = Long.valueOf( decodeMap.get("exp")) - DateUtil.currentTimestampSecond()-10*60L;
        return exp;
    }
    public static void setRestTemplate(RestTemplate restTemplate) {
        WjAccountClient.restTemplate = restTemplate;
    }

    public static void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        WjAccountClient.stringRedisTemplate = stringRedisTemplate;
    }
}
