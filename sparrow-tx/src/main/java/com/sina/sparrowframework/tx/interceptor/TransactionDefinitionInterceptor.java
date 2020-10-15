package com.sina.sparrowframework.tx.interceptor;

import com.sina.sparrowframework.tx.holder.TransactionDefinitionHolder;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
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
import org.springframework.util.Assert;


/**
 * @see TransactionDefinitionHolder
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
        Assert.notNull( transactionAttributeSource, "transactionAttributeSource config error." );
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Class<?> targetClass = (invocation.getThis() != null ? AopUtils.getTargetClass( invocation.getThis() ) : null);

        if (targetClass != null && !TransactionSynchronizationManager.isActualTransactionActive()) {

            LOG.debug( "==>outer transaction start:{},{}.", invocation.getMethod(), targetClass );

            TransactionDefinition definition = transactionAttributeSource.getTransactionAttribute(invocation.getMethod(), targetClass );

            if (definition != null && definition.getPropagationBehavior() != TransactionDefinition.PROPAGATION_NEVER) {
                TransactionDefinitionHolder.set(definition, invocation.getMethod());
            }

        } else {

            LOG.debug( "==>inner transaction start:{},{}.", invocation.getMethod(), targetClass );
        }

        Throwable ex = null;
        Object result = null;
        try {

            result = invocation.proceed();

        } catch (Throwable throwable) {
            ex = throwable;
        } finally {
            if (!TransactionSynchronizationManager.isActualTransactionActive()) {
                LOG.debug( "<==outer transaction end:{},{}.", invocation.getMethod(), targetClass );
                TransactionDefinitionHolder.clear();
            } else {
                LOG.debug( "<==inner transaction end:{},{}.", invocation.getMethod(), targetClass );
            }

        }

        if (ex != null) {
            throw ex;
        }

        return result;
    }


}
