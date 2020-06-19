package com.sina.sparrowframework.poi.annotation;

import java.lang.annotation.*;

/**
 * 这个注解 用于表示 一个 excel sheet 的 header
 * created  on 2018/7/18.
 *
 * @see ExcelMeta
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SheetHeader {

    /**
     * 定义 excel header 的名称
     */
    String value();

    /**
     * 从 0 开始
     */
    int index();

}
