package com.sina.sparrowframework.password.wjdata.data;

import java.io.Serializable;

public class Experiment implements Serializable {

    private static final long serialVersionUID = 5213271231369080491L;

    /**
     * 名称 必须
     */
    private String name;

    /**
     * 状态（not_assigned:未开始;new_assigned:新分组;cache:命中历史;force:强制名单;in_group:互斥组;offline:已下线；） 必须
     */
    private String status;

    /**
     * 分组 非必须
     */
    private String assignment;

    public String getName() {
        return name;
    }

    public Experiment setName(String name) {
        this.name = name;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public Experiment setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getAssignment() {
        return assignment;
    }

    public Experiment setAssignment(String assignment) {
        this.assignment = assignment;
        return this;
    }
}
