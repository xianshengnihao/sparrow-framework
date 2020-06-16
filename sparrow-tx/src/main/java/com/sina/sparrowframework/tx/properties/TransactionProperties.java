package com.sina.sparrowframework.tx.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "sparrow.transaction")
public class TransactionProperties {

    private Boolean enabled;
    private Boolean rocketMq;

    public Boolean getRocketMq() {
        return rocketMq;
    }

    public void setRocketMq(Boolean rocketMq) {
        this.rocketMq = rocketMq;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
