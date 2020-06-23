package com.sina.sparrowframework.mybatis.typehandler;

import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.sina.sparrowframework.tools.struct.CodeEnum;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class RegisterEnumHandlerConfig implements ConfigurationCustomizer {

    private static final Logger log = LoggerFactory.getLogger(RegisterEnumHandlerConfig.class);
    @Override
    public void customize(Configuration configuration) {
        log.debug("ConfigurationCustomizer init....");

        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .setScanners(
                                new TypeAnnotationsScanner(), // 设置Annotation的Scanner.
                                new SubTypesScanner(false) // 设置扫描子类型的scanner.
                        )
                        .setUrls(ClasspathHelper.forPackage("com.sina")) // 设置需要扫描的包，虽然指定了包路径，但是其实还是扫描整个root路径.
                        .filterInputsBy(new FilterBuilder().includePackage("com.sina")) // 因为上面的原因，所以这里加上了inputs过滤器
        );
        Set<Class<? extends CodeEnum>> subTypesOf = reflections.getSubTypesOf(CodeEnum.class);
        //TODO 注册的时候需要做下处理，防止一些非映射类不符合要求枚举注册导致报错
        subTypesOf.forEach((clazz) -> typeHandlerRegistry.register(clazz, SparrowEnumTypeHandler.class));
    }
}
