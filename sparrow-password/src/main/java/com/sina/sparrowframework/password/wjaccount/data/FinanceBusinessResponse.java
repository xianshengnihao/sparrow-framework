package com.sina.sparrowframework.password.wjaccount.data;

import com.sina.sparrowframework.tools.utility.JacksonUtil;

import java.io.Serializable;

/**
 * Created by wxn on 2021/4/15
 */
public class FinanceBusinessResponse<T> implements Serializable {

    private ControlResponse control;

    private T data;


    public T getData() {
        return data;
    }

    public FinanceBusinessResponse setData(T data) {
        this.data = data;
        return this;
    }

    public ControlResponse getControl() {
        return control;
    }

    public void setControl(ControlResponse control) {
        this.control = control;
    }

    public boolean isSuccess() {
        return "0".equals(this.control.getCode());
    }

    @Override
    public String toString() {
        return JacksonUtil.objectToJson(this);
    }
}
