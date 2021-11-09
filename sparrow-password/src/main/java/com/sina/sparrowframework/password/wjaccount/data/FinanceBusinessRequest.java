package com.sina.sparrowframework.password.wjaccount.data;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by wxn on 2021/4/15
 */
public class FinanceBusinessRequest {
    /**
     * 微博ID
     */
    @JsonIgnore
    private String wbId;

    public String getWbId() {
        return wbId;
    }

    public FinanceBusinessRequest setWbId(String wbId) {
        this.wbId = wbId;
        return this;
    }
}
