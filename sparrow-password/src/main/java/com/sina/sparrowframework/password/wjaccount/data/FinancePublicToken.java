package com.sina.sparrowframework.password.wjaccount.data;

import java.io.Serializable;

/**
 * Created by wxn on 2021/4/21
 */
public class FinancePublicToken implements Serializable {

    private String publicToken;
    private String publicAesKey;

    public String getPublicToken() {
        return publicToken;
    }

    public void setPublicToken(String publicToken) {
        this.publicToken = publicToken;
    }

    public String getPublicAesKey() {
        return publicAesKey;
    }

    public void setPublicAesKey(String publicAesKey) {
        this.publicAesKey = publicAesKey;
    }
}
