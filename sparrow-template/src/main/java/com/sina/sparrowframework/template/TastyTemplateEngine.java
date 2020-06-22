package com.sina.sparrowframework.template;

import org.springframework.core.io.Resource;


/**
 * 这个接口是 对 模板 库的抽象,如: Thymeleaf.
 * 将会利用底层模板 和 变量生成相应的 html or pdf
 * created  on  2019-05-02.
 */
public interface TastyTemplateEngine {


    /**
     * 将指定的 html 模块使用指定的变量替换,输入 html 文件资源
     *
     * @throws TemplateEngineException 模板转换出错
     */
    Resource render( EngineForm form) throws TemplateEngineException;


    /**
     * 将指定的 html 模块使用指定的变量替换
     *
     * @throws TemplateEngineException 模板转换出错
     */
    String renderAsString(EngineForm form) throws TemplateEngineException;

}
