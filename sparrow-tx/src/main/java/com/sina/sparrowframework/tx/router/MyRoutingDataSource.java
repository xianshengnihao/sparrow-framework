package com.sina.sparrowframework.tx.router;


import com.sina.sparrowframework.tx.helper.STM;
import com.sina.sparrowframework.tx.holder.TransactionDefinitionHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.annotation.Transactional;

/**
 * 路由数据源
 *
 * @see Transactional#readOnly()
 */
public class MyRoutingDataSource extends AbstractRoutingDataSource implements STM {

    private static final Logger LOG = LoggerFactory.getLogger(MyRoutingDataSource.class);

    private static final String MASTER_PATTER = "m%s";

    private static final String SLAVER_PATTER = "s%s";

    /***
     * 统计查询用的超时时间, 可决定是否使用专用于统计的数据
     */
    public static final int STATISTICS_TIMEOUT = 60 * 20;

    /**
     * 确定当前查找键。通常用于检查线程绑定的事务上下文。
     * <p>允许任意键，返回的键需要匹配存储的查找键类型，如
     * {@link #resolveSpecifiedLookupKey} 方法。
     */
    @Override
    protected Object determineCurrentLookupKey() {
        String pattern;
        String index = "0";
        if (TransactionDefinitionHolder.isReadOnly()) {
            pattern = SLAVER_PATTER;
            String txName = TransactionDefinitionHolder.getTxName();
            LOG.debug("current transaction name:{}", txName);
            if (txName.equals(ROC_TX_MANAGER)) {
                index = "2";
            } else if (txName.equals(DRAGON_TX_MANAGER)) {
                index = "3";
            } else if (txName.equals(PANGOLIN_TX_MANAGER)) {
                index = "4";
            } else if (txName.equals(JELLY_TX_MANAGER)) {
                index = "5";
            } else if (txName.equals(MY_CAT_MANAGER)) {
                index = "6";
            }else if (txName.equals(REWARD_MANAGER)) {
                index = "7";
            }
        } else {
            pattern = MASTER_PATTER;
        }
        String lookupKey = String.format(pattern, index);
        LOG.debug("datasource : {},thread :{}", lookupKey, Thread.currentThread().getName());
        return lookupKey;
    }
}
