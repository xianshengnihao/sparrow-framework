package com.sina.sparrowframework.tx.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "sparrowframework.transaction")
public class TransactionProperties {

    private Boolean enabled;
    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
