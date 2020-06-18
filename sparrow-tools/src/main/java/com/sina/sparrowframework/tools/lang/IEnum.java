package com.sina.sparrowframework.tools.lang;

import java.io.Serializable;

/**
 * 自定义枚举接口
 * @param <T>
 */
public interface IEnum<T extends Serializable> {
    /**
     * 枚举数据库存储值
     */
    T getValue();
}