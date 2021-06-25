package com.sina.sparrowframework.password.sinafud.data;

import java.io.Serializable;

/**
 * @author wxn
 */
public class FundBusinessResponse<T> implements Serializable {

    private static final long serialVersionUID = -5355895629367443579L;

    private String code;

    private String msg;

    private T data;

    private String sign;

    public String getCode() {
        return code;
    }

    public FundBusinessResponse setCode(String code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public FundBusinessResponse setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public T getData() {
        return data;
    }

    public FundBusinessResponse setData(T data) {
        this.data = data;
        return this;
    }

    public String getSign() {
        return sign;
    }

    public FundBusinessResponse setSign(String sign) {
        this.sign = sign;
        return this;
    }

    public boolean isSuccess() {
        return this.code == "0";
    }
}
