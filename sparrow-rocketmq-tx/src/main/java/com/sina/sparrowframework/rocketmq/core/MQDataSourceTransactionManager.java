package com.sina.sparrow.rocketmq.core;

import lombok.extern.slf4j.Slf4j;
import org.jcp.xml.dsig.internal.dom.DOMTransform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;

import javax.sql.DataSource;


/**
 * 自定义事务管理器
 */
public class MQDataSourceTransactionManager extends DataSourceTransactionManager{

    private static final long serialVersionUID = 1L;

    @Autowired
    private CurrentSendMqHolder currentSendMqHolder;

    public MQDataSourceTransactionManager() {}

    public MQDataSourceTransactionManager(DataSource dataSource) {
        this();
        setDataSource(dataSource);
        afterPropertiesSet();
    }

    @Override
    protected Object doGetTransaction() {
        CurrentSendMqHolder.set(Boolean.TRUE);
        return super.doGetTransaction();
    }

    /**
     * 事务成功
     * @param transaction
     */
    @Override
    protected void doCleanupAfterCompletion(Object transaction) {
        super.doCleanupAfterCompletion(transaction);
        this.currentSendMqHolder.sendFinalTransatcionMessage(true);
    }

    /**
     * 事务失败回滚
     * @param status
     */
    @Override
    protected void doRollback(DefaultTransactionStatus status) {
        super.doRollback(status);
        this.currentSendMqHolder.sendFinalTransatcionMessage(false);
    }



}

