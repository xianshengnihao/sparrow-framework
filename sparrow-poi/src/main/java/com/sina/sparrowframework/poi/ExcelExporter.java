package com.sina.sparrowframework.poi;


import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * 这个接口负责创建 excel 并将 数据加到 excel 中.
 * <p>
 * 此接口并不是线程安全的.
 * </p>
 *
 * @see com.sina.sparrowframework.poi.annotation.SheetHeader
 * @see com.sina.sparrowframework.poi.annotation.ExcelMeta
 */
public interface ExcelExporter {


    /**
     * 向 excel 加追加数据
     *
     * @param list 数据
     */
    <T> ExcelExporter appendData(final List<T> list, Class<T> elementClass);

    /**
     * 将 excel 导出为 Http 实体
     */
    HttpEntity<Resource> exportToHttpEntity() throws IOException;

    /**
     * 将 excel 导出为 Http 实体
     */
    HttpEntity<Resource> exportToHttpEntity(String excelName) throws IOException;

    /**
     * 将 excel 导出为 资源对象
     */
    Resource exportAsResource() throws IOException;

    /**
     * 创建 {@link ExcelExporter} 实现
     *
     * @param dataClass 用于表示数据的 class
     */
    static <T> ExcelExporter build(Class<T> dataClass) {
        return new DefaultExcelExporter(Collections.singletonList(dataClass));
    }


    /**
     * 创建 {@link ExcelExporter} 实现
     *
     * @param dataClassList 用于表示数据的 dto 的类型列表 ,不同的 class 在不同的 sheet
     */
    static ExcelExporter build(List<Class<?>> dataClassList) {
        return new DefaultExcelExporter(dataClassList);
    }
}
