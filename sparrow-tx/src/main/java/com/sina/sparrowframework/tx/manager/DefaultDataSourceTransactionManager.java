package com.sina.sparrowframework.tx.manager;

import com.sina.sparrowframework.tx.helper.Constants;
import com.sina.sparrowframework.tx.helper.STM;
import com.sina.sparrowframework.tx.holder.EnvironmentHolder;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionStatus;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;


/**
 * <p>自定义事务管理器</p>
 * <p>用于事务 开启前/成功/回滚的相应处理</p>
 */
public class DefaultDataSourceTransactionManager extends DataSourceTransactionManager {

    private static final long serialVersionUID = 1L;

    public DefaultDataSourceTransactionManager() {
    }

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
     *
     * @param transaction
     */
    @Override
    protected void doCleanupAfterCompletion(Object transaction) {
        super.doCleanupAfterCompletion(transaction);
    }

    /**
     * 事务失败回滚
     *
     * @param status
     */
    @Override
    protected void doRollback(DefaultTransactionStatus status) {
        super.doRollback(status);
    }

    /**
     * @param con
     * @param definition
     * @throws SQLException
     */
    @Override
    protected void prepareTransactionalConnection(Connection con, TransactionDefinition definition) throws SQLException {
        Environment env = EnvironmentHolder.getEnvironment();
        List<?> supportedLst = env.getProperty(Constants.PROJECT_SUPPORTED_MULTIPLE_TX_MANAGER,
                List.class, Arrays.asList(STM.JELLY_TX_MANAGER));
        if (supportedLst.contains(STM.JELLY_TX_MANAGER)) {
            String timeoutKey;
            try (Statement stmt = con.createStatement()) {
                timeoutKey = String.format("%s.max.wait.timeout", STM.JELLY_TX_MANAGER);
                Long timeout = env.getProperty(timeoutKey, Long.class, 28800L);
                stmt.execute(String.format("SET SESSION wait_timeout = %s", timeout));
            }
        }
        super.prepareTransactionalConnection(con, definition);
    }
}

