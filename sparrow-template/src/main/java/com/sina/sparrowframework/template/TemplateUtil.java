package com.sina.sparrowframework.template;

import com.sina.sparrowframework.tools.utility.ReflectionUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.*;

import static org.springframework.beans.BeanUtils.getPropertyDescriptors;

public abstract class TemplateUtil {

    /**
     * 等价于 {@link #copyToMap(Object bean, Class bean.getClass, boolean false, String[] EMPTY_STRING_ARRAY)}
     */
    public static Map<String, Object> copyToMap(final Object bean) {

        return copyToMap( bean, null, false, ArrayUtils.EMPTY_STRING_ARRAY );
    }

    /**
     * 将属性 copy 到 map
     *
     * @param bean             not null
     * @param editable         可 null,若传入 bean 的基类,则只 copy 基类的属性
     * @param outNullValue     true 则 值为 null 的属性也输出
     * @param ignoreProperties 要忽略的属性
     * @return map not null
     */
    public static Map<String, Object> copyToMap(final Object bean, Class<?> editable, boolean outNullValue, String... ignoreProperties) {
        Class<?> actualEditable = bean.getClass();
        if (editable != null) {
            if (!editable.isInstance( bean )) {
                throw new IllegalArgumentException( "Target class [" + bean.getClass().getName() +
                        "] not assignable to Editable class [" + editable.getName() + "]" );
            }
            actualEditable = editable;
        }
        PropertyDescriptor[] targetPds = getPropertyDescriptors( actualEditable );
        List<String> ignoreList = (ignoreProperties != null ? Arrays.asList( ignoreProperties ) : Collections.emptyList());
        Map<String, Object> map = new HashMap<>( (int) (targetPds.length / 0.75f) );

        Object value;
        for (PropertyDescriptor targetPd : targetPds) {
            Method readMethod = targetPd.getReadMethod();

            if (ignoreList.contains( targetPd.getName() ) || readMethod == null) {
                continue;
            }
            if (ReflectionUtils.isGetter( readMethod )) {
                //只取公共 getter 的值
                value = ReflectionUtils.invokeMethod( readMethod, bean );
                if (outNullValue || value != null) {
                    map.put( targetPd.getName(), value );
                }
            }
        }

        return map;
    }
}
