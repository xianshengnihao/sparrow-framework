package com.sina.sparrowframework.tx.interceptor;

import com.sina.sparrowframework.tools.utility.Assert;
import com.sina.sparrowframework.tx.holder.TransactionDefinitionHolder;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;


/**
 * @see TransactionDefinitionInterceptor
 */
public class TransactionDefinitionInterceptor implements MethodInterceptor, InitializingBean, ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger( TransactionDefinitionInterceptor.class );
    private TransactionAttributeSource transactionAttributeSource;
    private ApplicationContext applicationContext;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        transactionAttributeSource = applicationContext.getBean( TransactionAttributeSource.class );
        Assert.notNull( transactionAttributeSource, "config error" );
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Class<?> targetClass = (invocation.getThis() != null ? AopUtils.getTargetClass( invocation.getThis() ) : null);

        if (targetClass != null && !TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionDefinition definition = transactionAttributeSource.getTransactionAttribute(invocation.getMethod(), targetClass );

            if (definition != null && definition.getPropagationBehavior() != TransactionDefinition.PROPAGATION_NEVER) {
                TransactionDefinitionHolder.set(definition, invocation.getMethod());
            }
        }
        Throwable ex = null;
        Object result = null;

        try {
            result = invocation.proceed();
        } catch (Throwable throwable) {
            LOG.error(ExceptionUtils.getStackTrace(throwable));
        } finally {

            if (!TransactionSynchronizationManager.isActualTransactionActive()) {
                // 事务结束
                TransactionDefinitionHolder.clear();
            }
        }
        if (ex != null) {
            LOG.error(ExceptionUtils.getStackTrace(ex));
        }
        return result;
    }


}
