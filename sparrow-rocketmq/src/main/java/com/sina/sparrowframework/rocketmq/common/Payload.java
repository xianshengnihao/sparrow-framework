package com.sina.sparrowframework.rocketmq.common;

import java.io.Serializable;

/**
 * <p>负载（英语：Payload）是数据传输中所欲传输的实际信息，通常也被称作实际数据或者数据体。</p>
 * <p>信头与元数据，或称为开销数据，仅用于辅助数据传输</p>
 */
public class Payload implements Serializable {
    private String requestNo;
    private Long userId;
    private Long accountId;

    public String getRequestNo() {
        return requestNo;
    }

    public void setRequestNo(String requestNo) {
        this.requestNo = requestNo;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }
}
