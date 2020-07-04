package com.sina.sparrowframework.metadata;

import java.util.Optional;

import static com.sina.sparrowframework.metadata.constants.BaseCode.SUCCESS;

/**
 * Created by wxn on 2018/9/12
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ResponseResult {
    private String code;
    private String msg;
    private Object data;

    public String getCode() {
        return code;
    }

    public ResponseResult setCode(String code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public ResponseResult setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public Object getData() {
        return this.data;
    }

    public ResponseResult setData(Object data) {
        this.data = data;
        return this;
    }

    public static ResponseResult error(String code, String msg) {
        ResponseResult result = new ResponseResult();
        result.setCode(code);
        result.setMsg(Optional.ofNullable(msg).orElse(""));
        return result;
    }

    public static ResponseResult success(String msg, Object data) {
        ResponseResult result = new ResponseResult();
        result.setCode(SUCCESS.getCode());
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    /**
     * 操作成功（默认描述）
     */
    public static ResponseResult success(Object data) {
        ResponseResult result = new ResponseResult();
        result.setCode(SUCCESS.getCode());
        result.setMsg("成功");
        result.setData(data);
        return result;
    }

    /**
     * 操作成功（默认描述）
     */
    public static ResponseResult success() {
        ResponseResult result = new ResponseResult();
        result.setCode(SUCCESS.getCode());
        result.setMsg("成功");
        return result;
    }

    public boolean isSucceed() {
        return SUCCESS.getCode().equals(code);
    }

}



