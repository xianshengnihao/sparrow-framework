package com.sina.sparrowframework.tx.interceptor;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sina.sparrowframework.tools.utility.Assert;
import com.sina.sparrowframework.tools.utility.EncryptUtil;
import com.sina.sparrowframework.tools.utility.ObjectToolkit;
import com.sina.sparrowframework.tools.utility.StrToolkit;
import com.sina.sparrowframework.tx.annotation.Decryption;
import com.sina.sparrowframework.tx.annotation.Encryption;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 *<p>拦截器，用于处理加解密数据<p/>
 * @author: songbo1
 */
@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
        , @Signature(type = Executor.class, method = "query", args = {MappedStatement.class,
        Object.class, RowBounds.class, ResultHandler.class})})
public class EncryptionAndDecryptionInterceptor implements Interceptor {

    private static final String GET = "get";
    private static final String SET = "set";
    private static final String QUERY = "query";
    private static final String UPDATE = "update";
    private static final String ET = "et";

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        String methodName = invocation.getMethod().getName();
        Object[] args = invocation.getArgs();

        if ( args == null || args.length ==0 || args.length < 2) {
            return invocation.proceed();
        }
        Object returnValue = null;

        if (methodName.equals(QUERY)) {
            returnValue = invocationArgsHandler(invocation , methodName);

            if (returnValue instanceof List) {
                returnValue = this.locationInvoke(returnValue , 0);

            }else if (returnValue instanceof IPage) {
                returnValue = this.locationInvoke(returnValue , 1);
            }

        } else if (methodName.equals(UPDATE)) {
            returnValue = invocationArgsHandler(invocation , methodName);
        }
        return returnValue;
    }

    @Override
    public Object plugin(Object target) { return Plugin.wrap(target, this); }

    @Override
    public void setProperties(Properties properties) {}


    /**
     *
     * @param invocation
     * @return Object
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public Object invocationArgsHandler(Invocation invocation , String methodName) throws
            NoSuchMethodException
            , IllegalAccessException
            , InvocationTargetException
    {

        Assert.notNull(methodName , "methodName must not be null.");
        Object query_param = invocation.getArgs()[1];

        if (ObjectToolkit.isNotEmpty(query_param)) {
            Class<?> query_clazz = query_param.getClass();

            if (query_param instanceof MapperMethod.ParamMap  && UPDATE.equals(methodName)) {
                MapperMethod.ParamMap paramMap = (MapperMethod.ParamMap) query_param;
                Object et = paramMap.getOrDefault(ET, null);

                if (null != et) {
                    query_clazz = et.getClass();
                    query_param = et;
                }
            }
            Encryption en =  query_clazz.getAnnotation(Encryption.class);
            if (null != en) {
                String[] fields = en.value();
                for (String field : fields) {
                    this.methodInvoke(field , query_clazz , query_param ,0);
                }
            }
        }

        return invocation.proceed();

    }

    /**
     *
     * @param field
     * @param clazz
     * @param model
     * @param EN
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private void methodInvoke(String field , Class<?> clazz , Object  model , int EN ) throws
            NoSuchMethodException
            , InvocationTargetException
            , IllegalAccessException
    {

        String upper = field.substring(0, 1).toUpperCase() + field.substring(1);

        Method get = clazz.getMethod(GET.concat(upper) , null);
        Method set = clazz.getMethod(SET.concat(upper) , get.getReturnType());

        String value = (String)get.invoke(model, null);
        if (StrToolkit.isNotBlank(value) ){
            set.invoke(model, EN == 0 ? EncryptUtil.encryptText(value) : EncryptUtil.decryptText(value));
        }
    }

    /**
     * @param returnValue
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private Object locationInvoke(Object returnValue , int TYPE) throws
            NoSuchMethodException
            , IllegalAccessException
            , InvocationTargetException
    {

        List emp_list = (List)returnValue;
        int len = emp_list.size();
        if (len > 0) {
            Class<?> clazz = emp_list.get(0).getClass();
            Decryption dn = clazz.getAnnotation(Decryption.class);
            if (null != dn) {
                List<Object> pageList = new ArrayList<>();
                String[] fields = dn.value();
                for (int i = 0; i < len; i++) {
                    Object model = emp_list.get(i);
                    for (String field : fields) {
                        this.methodInvoke(field , clazz , model ,1);
                    }
                    pageList.add(model);
                }
                IPage<Object> iPage;
                if (TYPE == 1) {
                    iPage = new Page<>();
                    iPage.setRecords(pageList);
                    return iPage;

                }else {
                    return pageList;
                }
            }
        }
        return returnValue;
    }
}
