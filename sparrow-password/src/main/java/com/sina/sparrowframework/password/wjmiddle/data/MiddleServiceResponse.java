package com.sina.sparrowframework.password.wjmiddle.data;

import com.sina.sparrowframework.tools.utility.JacksonUtil;

/**
 * @author wxn
 * @date 2021/6/10 10:57 上午
 */
public class MiddleServiceResponse <T>{
    private String code;
    private String msg;
    private T data;

    public T getData() {
        return data;
    }

    public MiddleServiceResponse setData(T data) {
        this.data = data;
        return this;
    }
    public boolean isSuccess() {
        return "0".equals(this.getCode());
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return JacksonUtil.objectToJson(this);
    }
}
