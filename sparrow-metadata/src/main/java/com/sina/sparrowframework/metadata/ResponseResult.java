package com.sina.sparrowframework.metadata;

import com.sina.sparrowframework.tools.utility.JsonUtils;

import java.io.Serializable;
import java.util.Optional;

/**
 * Created by wxn on 2018/9/12
 */
public class ResponseResult<T> implements Serializable {
    private static final String SUCCESS_CODE = "000000";
    private String code;
    private String msg;
    private T data;

    private boolean succeed;

    public void setSucceed(boolean succeed) {
        this.succeed = succeed;
    }

    public ResponseResult() {
    }

    public ResponseResult(ResponseResult<T> old) {
        if (null != old.getCode()) {
            this.code = new String(old.code);
        }

        if (null != old.code && !"".equals(old.getMsg())) {
            this.msg = new String(old.getCode());
        }

        if (null != old.getData()) {
            this.data = old.getData();
        }

    }

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
        ResponseResult<T> result = new ResponseResult();
        result.setCode(code);
        result.setMsg(Optional.ofNullable(msg).orElse(""));
        return result;
    }

    public static <T> ResponseResult<T> success(String msg, T data) {
        ResponseResult<T> result = new ResponseResult();
        result.setCode(SUCCESS_CODE);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    /**
     * 操作成功（默认描述）
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> ResponseResult<T> success(T data) {
        ResponseResult<T> result = new ResponseResult();
        result.setCode(SUCCESS_CODE);
        result.setMsg("成功");
        result.setData(data);
        return result;
    }

    /**
     * 操作成功（默认描述）
     *
     * @return
     */
    public static ResponseResult success() {
        ResponseResult result = new ResponseResult();
        result.setCode(SUCCESS_CODE);
        result.setMsg("成功");
        return result;
    }

    public boolean isSucceed() {
        return SUCCESS_CODE.equals(this.code);
    }

    @Override
    public String toString() {
        return JsonUtils.writeToJson(this);
    }
}



