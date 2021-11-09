package com.sina.sparrowframework.password.wjaccount.data;

/**
 * Created by wxn on 2021/4/20
 */
public class ControlResponse {

    private String code;

    private String message;

    private String serverTime;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getServerTime() {
        return serverTime;
    }

    public void setServerTime(String serverTime) {
        this.serverTime = serverTime;
    }
}
