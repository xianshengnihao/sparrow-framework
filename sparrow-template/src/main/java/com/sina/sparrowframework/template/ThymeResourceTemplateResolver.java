package com.sina.sparrowframework.template;

import com.sina.sparrowframework.tools.utility.ArrayUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.StringUtils;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresource.ITemplateResource;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;

import static org.springframework.util.ResourceUtils.*;

/**
 * 这个类是 {@link ITemplateResolver} 的一个实现,主要用于定制 thymeleaf 的资源解析.
 * 以方便 邮件模板和协议模板的解析.
 * created  on 2018-12-18.
 * @see ITemplateEngine
 */
public class ThymeResourceTemplateResolver extends AbstractConfigurableTemplateResolver implements ApplicationContextAware {

    /**
     * 表示 从 {@link org.springframework.core.env.Environment} 中直接或间接获取资源
     */
    public static final String ENV_PREFIX = "env:";

    /**
     * 表示 云存储中获取资源.
     */
    public static final String CLOUD_PREFIX = "cloud:";

    public static final Set<String> RESOURCE_PREFIX_SET = ArrayUtils.asUnmodifiableSet(
            CLASSPATH_URL_PREFIX,
            FILE_URL_PREFIX,
            JAR_URL_PREFIX,
            ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX,
            ENV_PREFIX,
            CLOUD_PREFIX
    );

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    @Override
    protected String computeResourceName(IEngineConfiguration configuration, String ownerTemplate, String template,
                                         String prefix, String suffix, boolean forceSuffix,
                                         Map<String, String> templateAliases,
                                         Map<String, Object> templateResolutionAttributes) {

        String resourceName = template;
        if (!containsPrefix(resourceName)) {
            resourceName = super.computeResourceName(configuration, ownerTemplate, template, prefix
                    , suffix, forceSuffix, templateAliases, templateResolutionAttributes);

            if (!containsPrefix(resourceName)) {
                resourceName = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + resourceName;
            }
        }

        return resourceName;

    }

    @Override
    protected ITemplateResource computeTemplateResource(
            final IEngineConfiguration configuration, final String ownerTemplate, final String template
            , final String resourceName, final String characterEncoding
            , final Map<String, Object> templateResolutionAttributes) {
        try {
            return new ThymeleafTemplateResource(this.applicationContext, resourceName, Charset.forName(characterEncoding));
        } catch (IOException e) {
            return null;
        }
    }

    public static boolean containsPrefix(String location) {
        if (!StringUtils.hasText(location)) {
            return false;
        }
        int index = location.indexOf(':');
        boolean contains = false;
        if (index > 0) {
            contains = RESOURCE_PREFIX_SET.contains(location.substring(0, index + 1));
        }
        return contains;
    }

}
