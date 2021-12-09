package com.sina.sparrowframework.password.wjaccount.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author wxn
 * @date 2021/5/11 2:55 下午
 */
public abstract class WjAccountSignatureUtils {

    private static final Logger LOG = LoggerFactory.getLogger(WjAccountSignatureUtils.class);

    public final static String appIdName = "appId";
    public final static String appKeyName = "appKey";
    public final static String tokenName = "token";
    public final static String preMasterKeyName = "preMasterKey";
    public final static String cipherSuiteName = "cipherSuite";
    public final static String backMasterKeyName = "backMasterKey";
    public final static String validCipherKeyName = "encryptMasterKey";
    public final static String thirdIdName = "thirdId";
    public final static String loginTypeName = "loginType";
    public final static String xPassportTokenName = "xPassportToken";
    public final static String xPassportToken = "x-passport-token";
    public final static String xMerchantToken= "x-merchant-token";
    public final static String xPassportTimestamp= "x-passport-timestamp";

    public static final String PARAM_USER_AGENT = "User-Agent";
    public static final String PARAM_REFERER = "Referer";
    /**
     * 金融账户 商户token redis存储KEY
     */
    public final static String FINANCE_X_MERCHANT_TOKEN_REDIS_KEY= "sparrow:finance:XMerchantToken";

    /**
     * 金融账户 用户token redis存储KEY
     */
    public final static String FINANCE_USER_TOKEN_REDIS_KEY= "sparrow:finance:UserToken:%s";

    /**
     * 获取商户token
     */
    public final static String getTokenUrl = "/open/grant/getToken.do";
    /**
     *秘钥协商接口
     */
    public final static String getCipherKeyUrl = "/open/grant/getCipherKey.do";
    /**
     * 密钥验证
     */
    public final static String validCipherKeyUrl = "/open/grant/validCipherKey.do";
    /**
     * 登录获取用户token
     */
    public final static String loginUrl = "/open/third/login.do";


    public static final String DATA = "data";







}
