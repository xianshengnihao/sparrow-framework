package com.sina.sparrowframework.rocketmq.common;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum LocalTransactionStatus{


    NONE(0, "初始化"),

    SUCCESS(10, "成功"),

    FAILED(20, "失败");

    @EnumValue
    private final Integer code;

    private final String display;

    LocalTransactionStatus(Integer code, String display) {
        this.code = code;
        this.display = display;
    }


    public Integer getCode() {
        return code;
    }

    public String display() {
        return display;
    }


}
