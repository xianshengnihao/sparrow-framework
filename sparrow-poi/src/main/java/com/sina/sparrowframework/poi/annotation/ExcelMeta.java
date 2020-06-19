package com.sina.sparrowframework.poi.annotation;

import java.lang.annotation.*;

/**
 * created  on 2018/8/9.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelMeta {

    /**
     * excel 名称, ${date} 可表示当前日期(uuuu-MM-dd),
     */
    String name();

    /**
     * sheet 名称,默认为 {@link #name()} 不包含 ${date}
     */
    String sheet() default "";
}
