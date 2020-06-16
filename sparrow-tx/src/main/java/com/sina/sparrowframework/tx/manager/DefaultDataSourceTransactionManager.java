package com.sina.sparrowframework.tx.manager;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;

import javax.sql.DataSource;


/**
 * <p>自定义事务管理器</p>
 * <p>用于事务 开启前/成功/回滚的相应处理</p>
 */
public class DefaultDataSourceTransactionManager extends DataSourceTransactionManager {

    private static final long serialVersionUID = 1L;

    public DefaultDataSourceTransactionManager() {}

    public DefaultDataSourceTransactionManager(DataSource dataSource) {
        this();
        setDataSource(dataSource);
        afterPropertiesSet();
    }

    @Override
    protected Object doGetTransaction() {
        return super.doGetTransaction();
    }

    /**
     * 事务成功
     * @param transaction
     */
    @Override
    protected void doCleanupAfterCompletion(Object transaction) {
        super.doCleanupAfterCompletion(transaction);
    }

    /**
     * 事务失败回滚
     * @param status
     */
    @Override
    protected void doRollback(DefaultTransactionStatus status) {
        super.doRollback(status);
    }

}

