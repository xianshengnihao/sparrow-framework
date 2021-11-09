package com.sina.sparrowframework.password.wjaccount.data;

/**
 * @author wxn
 * @date 2021/6/29 3:20 下午
 */
public class FinanceUserInfo {
    /**
     * 登录ID
     */
    private String lgId;
    /**
     * 注册ID
     */
    private String uid;

    public String getLgId() {
        return lgId;
    }

    public FinanceUserInfo setLgId(String lgId) {
        this.lgId = lgId;
        return this;
    }

    public String getUid() {
        return uid;
    }

    public FinanceUserInfo setUid(String uid) {
        this.uid = uid;
        return this;
    }
}
