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
    /**
     * 实名ID
     */
    private String pid;
    /**
     * 用户token
     */
    private String userToken;

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

    public String getPid() {
        return pid;
    }

    public FinanceUserInfo setPid(String pid) {
        this.pid = pid;
        return this;
    }

    public String getUserToken() {
        return userToken;
    }

    public FinanceUserInfo setUserToken(String userToken) {
        this.userToken = userToken;
        return this;
    }
}
