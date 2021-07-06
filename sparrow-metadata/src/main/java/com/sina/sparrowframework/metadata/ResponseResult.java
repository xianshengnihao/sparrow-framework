package com.sina.sparrowframework.metadata;

import java.io.Serializable;
import java.util.Optional;

import static com.sina.sparrowframework.metadata.constants.BaseCode.SUCCESS;

/**
 * Created by wxn on 2018/9/12
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ResponseResult<T> implements Serializable {
    private String code;
    private String msg;
    private T data;

    public String getCode() {
        return code;
    }

    public ResponseResult<T> setCode(String code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public ResponseResult<T> setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public T getData() {
        return this.data;
    }

    public ResponseResult<T> setData(T data) {
        this.data = data;
        return this;
    }

    public static <T> ResponseResult<T> error(String code, String msg) {
        ResponseResult<T> result = new ResponseResult<>();
        result.setCode(code);
        result.setMsg(Optional.ofNullable(msg).orElse(""));
        return result;
    }

    public static <T> ResponseResult<T> success(String msg, T data) {
        ResponseResult<T> result = new ResponseResult<>();
        result.setCode(SUCCESS.getCode());
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    public static <T> ResponseResult<T> success(String msg, T data, Class<T> responseDataType) {
        ResponseResult<T> result = new ResponseResult<>();
        result.setCode(SUCCESS.getCode());
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    /**
     * 操作成功（默认描述）
     */
    public static <T> ResponseResult<T> success(T data) {
        ResponseResult<T> result = new ResponseResult<>();
        result.setCode(SUCCESS.getCode());
        result.setMsg("成功");
        result.setData(data);
        return result;
    }

    /**
     * 操作成功（默认描述）
     */
    public static <T> ResponseResult<T> success() {
        ResponseResult<T> result = new ResponseResult<>();
        result.setCode(SUCCESS.getCode());
        result.setMsg("成功");
        return result;
    }

    public boolean isSucceed() {
        return SUCCESS.getCode().equals(code);
    }

}



