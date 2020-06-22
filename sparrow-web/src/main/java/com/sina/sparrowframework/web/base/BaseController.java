package com.sina.sparrowframework.web.base;

import com.sina.sparrowframework.metadata.ResponseResult;
import com.sina.sparrowframework.mybatis.internal.CommonConfig;

/**
 * 基础服务类
 *
 * @author tianye6
 * @date 2019/7/7 10:42
 */
public abstract class BaseController extends CommonConfig {

    public  <T> ResponseResult<T> success(T t) {
        return ResponseResult.success(t);
    }
    public  <T> ResponseResult<T> success() {
        return ResponseResult.success();
    }
    public ResponseResult error(String code, String msg) {
        return ResponseResult.error(code, msg);
    }

    public <T> ResponseResult<T> success(String msg, T data) {
        return ResponseResult.success(msg, data);
    }

}
