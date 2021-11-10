package com.sina.sparrowframework.password.wjcontract.data;

import com.sina.sparrowframework.tools.utility.JacksonUtil;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

/**
 * @Author liuyi
 * @Description
 * @Date 2021/11/8 14:23
 **/
public class WjContractRequest implements Serializable {

    private static final long serialVersionUID = 2429481876736725482L;
    private String uid;

    //private String appid;

    private Long timestamp = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));

    private Map<String, Object> body;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Map<String, Object> getBody() {
        return body;
    }

    public void setBody(Map<String, Object> body) {
        this.body = body;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return JacksonUtil.objectToJson(this);
    }

}
