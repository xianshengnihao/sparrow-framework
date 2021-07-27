package com.sina.sparrowframework.password.wjdata.data;

import com.sina.sparrowframework.tools.utility.JacksonUtil;

import java.io.Serializable;
import java.util.List;

public class WjDataABTestResponse implements Serializable {

    private static final long serialVersionUID = 2623808592708273300L;

    /**
     * 用户ID  非必须
     */
    private String user_id;

    /**
     * 应用类型，web	  非必须
     */
    private String app;

    /**
     * 实验信息	非必须
     */
    private List<Experiment> experiments;

    /**
     * 渠道	非必须
     */
    private String channel;

    public String getUser_id() {
        return user_id;
    }

    public WjDataABTestResponse setUser_id(String user_id) {
        this.user_id = user_id;
        return this;
    }

    public String getApp() {
        return app;
    }

    public WjDataABTestResponse setApp(String app) {
        this.app = app;
        return this;
    }

    public List<Experiment> getExperiments() {
        return experiments;
    }

    public WjDataABTestResponse setExperiments(List<Experiment> experiments) {
        this.experiments = experiments;
        return this;
    }

    public String getChannel() {
        return channel;
    }

    public WjDataABTestResponse setChannel(String channel) {
        this.channel = channel;
        return this;
    }

    @Override
    public String toString() {
        return JacksonUtil.objectToJson(this);
    }
}
