package com.sina.sparrowframework.password.wjdata.data;

import com.sina.sparrowframework.tools.utility.JacksonUtil;

import java.io.Serializable;
import java.util.List;

public class WjDataABTestRequest implements Serializable {

    private static final long serialVersionUID = 3651120957956614703L;

    /**
     * 唯一ID  必须
     */
    private String user_id;

    /**
     * 业务:business;风控:strategy;不传默认business  非必须
     */
    private String groupType;

    /**
     * 用户ID:userId; 设备ID:deviceId;不传默认userId  非必须
     */
    private String idType;

    /**
     * 渠道	必须
     */
    private String channel;

    /**
     * 应用端，web  必须
     */
    private String app;

    /**
     * 实验列表	必须
     */
    private List<String> experiments;

    public String getUser_id() {
        return user_id;
    }

    public WjDataABTestRequest setUser_id(String user_id) {
        this.user_id = user_id;
        return this;
    }

    public String getGroupType() {
        return groupType;
    }

    public WjDataABTestRequest setGroupType(String groupType) {
        this.groupType = groupType;
        return this;
    }

    public String getIdType() {
        return idType;
    }

    public WjDataABTestRequest setIdType(String idType) {
        this.idType = idType;
        return this;
    }

    public String getChannel() {
        return channel;
    }

    public WjDataABTestRequest setChannel(String channel) {
        this.channel = channel;
        return this;
    }

    public String getApp() {
        return app;
    }

    public WjDataABTestRequest setApp(String app) {
        this.app = app;
        return this;
    }

    public List<String> getExperiments() {
        return experiments;
    }

    public WjDataABTestRequest setExperiments(List<String> experiments) {
        this.experiments = experiments;
        return this;
    }

    @Override
    public String toString() {
        return JacksonUtil.objectToJson(this);
    }
}
