package com.sina.sparrowframework.job;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "xxl.job")
public class XxlJobProperties {

    private String adminAddresses;

    private String accessToken;

    private String executorAppname;

    private String executorAddress;

    private String executorIp;

    private int executorPort;

    private String executorLogPath;

    private int executorLogRetentionDays;

    public String getAdminAddresses() {
        return adminAddresses;
    }

    public XxlJobProperties setAdminAddresses(String adminAddresses) {
        this.adminAddresses = adminAddresses;
        return this;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public XxlJobProperties setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    public String getExecutorAppname() {
        return executorAppname;
    }

    public XxlJobProperties setExecutorAppname(String executorAppname) {
        this.executorAppname = executorAppname;
        return this;
    }

    public String getExecutorAddress() {
        return executorAddress;
    }

    public XxlJobProperties setExecutorAddress(String executorAddress) {
        this.executorAddress = executorAddress;
        return this;
    }

    public String getExecutorIp() {
        return executorIp;
    }

    public XxlJobProperties setExecutorIp(String executorIp) {
        this.executorIp = executorIp;
        return this;
    }

    public int getExecutorPort() {
        return executorPort;
    }

    public XxlJobProperties setExecutorPort(int executorPort) {
        this.executorPort = executorPort;
        return this;
    }

    public String getExecutorLogPath() {
        return executorLogPath;
    }

    public XxlJobProperties setExecutorLogPath(String executorLogPath) {
        this.executorLogPath = executorLogPath;
        return this;
    }

    public int getExecutorLogRetentionDays() {
        return executorLogRetentionDays;
    }

    public XxlJobProperties setExecutorLogRetentionDays(int executorLogRetentionDays) {
        this.executorLogRetentionDays = executorLogRetentionDays;
        return this;
    }
}
