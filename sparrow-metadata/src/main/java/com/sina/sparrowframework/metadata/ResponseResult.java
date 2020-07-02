package com.sina.sparrowframework.metadata;

import com.sina.sparrowframework.metadata.constants.CodeManager;
import com.sina.sparrowframework.tools.utility.JsonUtils;

import java.util.Optional;

import static com.sina.sparrowframework.metadata.constants.BaseCode.SUCCESS;

/**
 * Created by wxn on 2018/9/12
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ResponseResult {
    private CodeManager code;
    private String msg;
    private Object data;

    public CodeManager getCode() {
        return code;
    }

    public ResponseResult setCode(CodeManager code) {
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

    public static ResponseResult error(CodeManager code, String msg) {
        ResponseResult result = new ResponseResult();
        result.setCode(code);
        result.setMsg(Optional.ofNullable(msg).orElse(""));
        return result;
    }

    public static ResponseResult success(String msg, Object data) {
        ResponseResult result = new ResponseResult();
        result.setCode(SUCCESS);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    /**
     * 操作成功（默认描述）
     */
    public static ResponseResult success(Object data) {
        ResponseResult result = new ResponseResult();
        result.setCode(SUCCESS);
        result.setMsg("成功");
        result.setData(data);
        return result;
    }

    /**
     * 操作成功（默认描述）
     */
    public static ResponseResult success() {
        ResponseResult result = new ResponseResult();
        result.setCode(SUCCESS);
        result.setMsg("成功");
        return result;
    }

    public boolean isSucceed() {
        return SUCCESS == code;
    }

    @Override
    public String toString() {
        return JsonUtils.writeToJson(this);
    }
}



