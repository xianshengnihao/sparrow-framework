package com.sina.sparrowframework.template;

import com.sina.sparrowframework.metadata.constants.BaseCode;
import com.sina.sparrowframework.tools.struct.ResultCode;
import com.sina.sparrowframework.tools.utility.FileUtils;
import com.sina.sparrowframework.tools.utility.ReflectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import org.thymeleaf.util.FastStringWriter;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.*;

import static com.sina.sparrowframework.template.TemplateUtil.copyToMap;
import static com.sina.sparrowframework.template.ThymeResourceTemplateResolver.ENV_PREFIX;
import static com.sina.sparrowframework.tools.utility.Assert.*;
import static org.springframework.beans.BeanUtils.getPropertyDescriptors;


/**
 * 这个类是 {@link TastyTemplateEngine} 的一个实现.
 * created  on 2018-12-19.
 *
 * @see org.thymeleaf.ITemplateEngine
 * @see <a href=https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html">Thymeleaf Documentation: SET</a>
 */
public class ThymeleafTemplateEngineImpl implements TastyTemplateEngine, EnvironmentAware {

    private static final Logger LOG = LoggerFactory.getLogger(ThymeleafTemplateEngineImpl.class);


    private Environment env;

    private ITemplateEngine templateEngine;


    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }

    @Nullable
    @Override
    public Resource render(@NonNull EngineForm form) throws TemplateEngineException, IllegalArgumentException {
        return process(form);
    }


    @Nullable
    @Override
    public String renderAsString(@NonNull EngineForm form) throws TemplateEngineException, IllegalArgumentException {

        assertForm(form);

        try {
            FastStringWriter writer = new FastStringWriter();
            // 替换模板
            process(computeTemplateName(form.getTemplateName()), createContext(form), writer);

            String result = writer.toString();

            if (!StringUtils.hasText(result)) {
                throw new TemplateEngineException(BaseCode.ASSERT_ERROR,
                        String.format("没有找到相应模板,template[%s]", form.getTemplateName()));
            }
            return result;
        } catch (TemplateEngineException e) {
            throw e;
        } catch (Exception e) {
            throw new TemplateEngineException(BaseCode.ASSERT_ERROR, e.getMessage(), e);
        }
    }



    /*########################## 以下非接口方法 #####################################*/


    private void assertForm(EngineForm form) throws IllegalArgumentException {
        hasText(form.getTemplateName(), "templateName required");
        if (form.getTemplateName().startsWith(ENV_PREFIX)) {
            assertTrue(form.getTemplateName().length() > ENV_PREFIX.length(), "templateName error");
        }
        boolean bothNull = form.getVariables() == null && form.getVariableBean() == null;
        assertFalse(bothNull, "variables and variableBean both is nul");

    }


    /**
     * @param form required
     * @return null or resource
     */
    private Resource process(EngineForm form)
            throws IllegalArgumentException, TemplateEngineException {
        assertForm(form);
        String templateName = computeTemplateName(form.getTemplateName());
        IContext context = createContext(form);
        Resource resource;
        try {
            File tempFile = createTempFile();
            process(templateName, context, new FileWriter(tempFile));
            resource = new FileSystemResource(tempFile);
            return resource;
        } catch (Exception e) {
            throw new TemplateEngineException(BaseCode.ASSERT_ERROR, e.getMessage(), e);
        }
    }

    /**
     * 调用模板引擎
     */
    private void process(String templateName, IContext context, Writer writer) throws TemplateEngineException {
        try (Writer w = writer) {
            templateEngine.process(templateName, context, w);
        } catch (IOException e) {
            throw new TemplateEngineException(BaseCode.ASSERT_ERROR, e.getMessage(), e);
        }
    }


    private File createTempFile() throws IOException {
        File dir = new File(FileUtils.getTempDir(), "thymeleaf");
        if (!dir.exists() && dir.mkdir()) {
            LOG.debug("create temp dir {}", dir.getAbsolutePath());
        }
        File file = new File(dir, UUID.randomUUID().toString());
        if (!file.exists() && file.createNewFile()) {
            LOG.debug("create temp file {}", file.getAbsolutePath());
        }
        return file;
    }

    private IContext createContext(EngineForm form) {
        IContext context;
        if (form.getVariables() != null) {
            context = new Context(Locale.CHINA, form.getVariables());
        } else {
            context = new Context(Locale.CHINA, copyToMap(Objects.requireNonNull(form.getVariableBean())));
        }
        return context;
    }

    private String computeTemplateName(String template) throws IllegalArgumentException {
        String templateName = template;
        if (template.startsWith(ENV_PREFIX)) {
            templateName = env.getProperty(template.substring(ENV_PREFIX.length()));
            hasText(templateName, String.format("%s not config.", template));
        }
        return templateName;
    }




    /*############################ 以下是依赖 setter ###################################*/


    @Autowired
    public void setTemplateEngine(@Qualifier("templateEngine") ITemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

}
