package com.sina.sparrowframework.password.wjdata.data;

import com.sina.sparrowframework.tools.utility.JacksonUtil;

import java.io.Serializable;

public class WjDataBaseResponse<T> implements Serializable {

    private static final long serialVersionUID = -9038968341914964253L;

    private String code;

    private String msg;

    private T data;

    public String getCode() {
        return code;
    }

    public WjDataBaseResponse setCode(String code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public WjDataBaseResponse setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public T getData() {
        return data;
    }

    public WjDataBaseResponse setData(T data) {
        this.data = data;
        return this;
    }

    public boolean isSuccess() {
        return this.code.equals("0");
    }

    @Override
    public String toString() {
        return JacksonUtil.objectToJson(this);
    }
}
