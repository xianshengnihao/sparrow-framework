package com.sina.sparrowframework.password.sinafud.data;


import com.sina.sparrowframework.tools.utility.JacksonUtil;

import java.io.Serializable;

/**
 * @author wxn
 */
public class FundBusinessRequest implements Serializable {

    private static final long serialVersionUID = -1883065233068383151L;


    /**
     * 商户Key
     */
    private String merchant_key;

    /**
     * 商户平台用户ID
     */
    private String merchant_uid;

    private String sign;

    public String getMerchant_key() {
        return merchant_key;
    }

    public FundBusinessRequest setMerchant_key(String merchant_key) {
        this.merchant_key = merchant_key;
        return this;
    }

    public String getMerchant_uid() {
        return merchant_uid;
    }

    public FundBusinessRequest setMerchant_uid(String merchant_uid) {
        this.merchant_uid = merchant_uid;
        return this;
    }

    public String getSign() {
        return sign;
    }

    public FundBusinessRequest setSign(String sign) {
        this.sign = sign;
        return this;
    }

    @Override
    public String toString() {
        return JacksonUtil.objectToJson(this);
    }
}
