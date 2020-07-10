package com.sina.sparrowframework.tx.manager;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.sina.sparrowframework.tools.utility.ObjectToolkit;
import com.sina.sparrowframework.tx.condition.*;
import com.sina.sparrowframework.tx.helper.Constants;
import com.sina.sparrowframework.tx.helper.STM;
import com.sina.sparrowframework.tx.holder.EnvironmentHolder;
import com.sina.sparrowframework.tx.interceptor.TransactionDefinitionInterceptor;
import com.sina.sparrowframework.tx.router.MyRoutingDataSource;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>数据源核心配置类</p>
 * <p>加载时机要高于{@link MybatisPlusAutoConfiguration}</p>
 * <p>确保DataSource是singleton</p>
 */
@Configuration
@AutoConfigureBefore(MybatisPlusAutoConfiguration.class)
@ConditionalOnProperty(
        prefix = "sparrow.transaction",
        name = {"enabled"},
        havingValue = "true",
        matchIfMissing = true
)
public class DataSourceConfiguation extends BaseCondition implements EnvironmentAware, InitializingBean, STM {
    private Environment env;

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
        EnvironmentHolder.setEnvironment(environment);
    }

    @Override
    public void afterPropertiesSet() throws Exception {}

    /**
     * 编号为 0 的主库
     */
    @Bean(initMethod = "init", destroyMethod = "close")
    public DruidDataSource master0DataSource() {
        return createMasterDataSource(true, 0);
    }

    /**
     * 编号为 0 的从库
     */
    @Bean(initMethod = "init", destroyMethod = "close")
    public DruidDataSource slaver0DataSource() {
        return createMasterDataSource(false, 0);
    }

    /**
     * 编号为 2 的从库(Roc项目从库)
     */
    @Bean(name = "slaver2DataSource", initMethod = "init", destroyMethod = "close")
    @Conditional(S2DataSourceCondition.class)
    public DruidDataSource slaver2DataSource() {
        return createMasterDataSource(false, 2);
    }

    /**
     * 编号为 3 的从库(dragon项目从库)
     */
    @Bean(name = "slaver3DataSource", initMethod = "init", destroyMethod = "close")
    @Conditional(S3DataSourceCondition.class)
    public DruidDataSource slaver3DataSource() {
        return createMasterDataSource(false, 3);
    }

    /**
     * 编号为 4 的从库(pangolin项目从库)
     * 动态数据源
     */
    @Bean(name = "slaver4DataSource", initMethod = "init", destroyMethod = "close")
    @Conditional(S4DataSourceCondition.class)
    public DruidDataSource slaver4DataSource() {
        return createMasterDataSource(false, 4);
    }

    /**
     * 编号为 5 的从库(jelly项目从库)
     * 动态数据源
     */
    @Bean(name = "slaver5DataSource", initMethod = "init", destroyMethod = "close")
    @Conditional(S5DataSourceCondition.class)
    public DruidDataSource slaver5DataSource() {
        return createMasterDataSource(false, 5);
    }

    /**
     * 编号为 6 的myCaT库(mycat库)
     * 动态数据源
     */
    @Bean(name = "myCat6DataSource", initMethod = "init", destroyMethod = "close")
    @Conditional(MyCat6DataSourceCondition.class)
    public DruidDataSource myCat6DataSource() {
        return createMasterDataSource(false, 6);
    }

    /**
     * 编号为 7 的从库(reward项目从库)
     * 动态数据源
     */
    @Bean(name = "slaver7DataSource", initMethod = "init", destroyMethod = "close")
    @Conditional(S7DataSourceCondition.class)
    public DruidDataSource slaver7DataSource() {
        return createMasterDataSource(false, 7);
    }

    /**
     * 封装 数据源创建逻辑
     */
    private DruidDataSource createMasterDataSource(boolean master, int index) {
        String role = master ? Constants.master : Constants.slaver;
        DruidDataSource ds = new DruidDataSource();

        ds.setUrl(env.getRequiredProperty(String.format(Constants.url, role, index)));
        ds.setUsername(env.getRequiredProperty(String.format(Constants.username, role, index)));
        ds.setPassword(env.getRequiredProperty(String.format(Constants.password, role, index)));
        ds.setDriverClassName(env.getRequiredProperty(Constants.driver));

        ds.setInitialSize(env.getProperty(String.format(Constants.initialSize, role, index), Integer.class, 2));
        ds.setMaxActive(env.getProperty(String.format(Constants.maxActive, role, index), Integer.class, 50));
        ds.setMaxWait(env.getProperty(String.format(Constants.maxWait, role, index), Long.class, 27L * 1000L));
        ds.setValidationQuery(env.getProperty(String.format(Constants.validationQuery, role, index), "SELECT DATE() FROM dual"));

        ds.setTestOnBorrow(env.getProperty(String.format(Constants.testOnBorrow, role, index), Boolean.class, Boolean.FALSE));
        ds.setTestWhileIdle(env.getProperty(String.format(Constants.testWhileIdle, role, index), Boolean.class, Boolean.TRUE));
        ds.setTestOnReturn(env.getProperty(String.format(Constants.testOnReturn, role, index), Boolean.class, Boolean.FALSE));
        ds.setTimeBetweenEvictionRunsMillis(env.getProperty(String.format(Constants.timeBetweenEvictionRunsMillis, role, index), Long.class, 5L * 1000L));

        ds.setRemoveAbandoned(env.getProperty(String.format(Constants.removeAbandoned, role, index), Boolean.class, Boolean.FALSE));
        ds.setMinEvictableIdleTimeMillis(env.getProperty(String.format(Constants.minEvictableIdleTimeMillis, role, index), Long.class, 30000L));
        return ds;
    }

    /**
     * 路由数据库,负责将事务引入到具体的数据库
     */
    @Bean
    @Primary
    public DataSource dataSource() {
        MyRoutingDataSource dataSource = new MyRoutingDataSource();
        //默认数据源
        DataSource m0 = master0DataSource();
        DataSource s0 = slaver0DataSource();
        dataSource.setDefaultTargetDataSource(m0);
        Map<Object, Object> dsMap = new HashMap<>(8);
        dsMap.put(Constants.m0, m0);
        dsMap.put(Constants.s0, s0);
        if (isSupportDs(env, Constants.s2)) {
            DataSource s2 = slaver2DataSource();
            if (!ObjectToolkit.isEmpty(s2)) {
                dsMap.put(Constants.s2, s2);
            }
        }
        if (isSupportDs(env, Constants.s3)) {
            DataSource s3 = slaver3DataSource();
            if (!ObjectToolkit.isEmpty(s3)) {
                dsMap.put(Constants.s3, s3);
            }
        }
        if (isSupportDs(env, Constants.s4)) {
            DataSource s4 = slaver4DataSource();
            if (!ObjectToolkit.isEmpty(s4)) {
                dsMap.put(Constants.s4, s4);
            }
        }
        if (isSupportDs(env, Constants.s5)) {
            DataSource s5 = slaver5DataSource();
            if (!ObjectToolkit.isEmpty(s5)) {
                dsMap.put(Constants.s5, s5);
            }
        }
        if (isSupportDs(env, Constants.myCat6)) {
            DataSource myCat6 = myCat6DataSource();
            if (!ObjectToolkit.isEmpty(myCat6)) {
                dsMap.put(Constants.myCat6, myCat6);
            }
        }
        if (isSupportDs(env, Constants.s7)) {
            DataSource s7 = slaver7DataSource();
            if (!ObjectToolkit.isEmpty(s7)) {
                dsMap.put(Constants.s7, s7);
            }
        }
        dataSource.setTargetDataSources(dsMap);
        return dataSource;
    }

    @Bean(name = TX_MANAGER)
    @Primary
    public DefaultDataSourceTransactionManager txManager(@Qualifier("dataSource") DataSource dataSource) {
        return new DefaultDataSourceTransactionManager(dataSource);
    }

    @Bean(name = ROC_TX_MANAGER)
    @Conditional(S2DataSourceCondition.class)
    public DefaultDataSourceTransactionManager s2TxManager() {
        return new DefaultDataSourceTransactionManager(slaver2DataSource());
    }

    @Bean(name = DRAGON_TX_MANAGER)
    @Conditional(S3DataSourceCondition.class)
    public DefaultDataSourceTransactionManager s3TxManager() {
        return new DefaultDataSourceTransactionManager(slaver3DataSource());
    }

    @Bean(name = PANGOLIN_TX_MANAGER)
    @Conditional(S4DataSourceCondition.class)
    public DefaultDataSourceTransactionManager s4TxManager() {
        return new DefaultDataSourceTransactionManager(slaver4DataSource());
    }

    @Bean(name = JELLY_TX_MANAGER)
    @Conditional(S5DataSourceCondition.class)
    public DefaultDataSourceTransactionManager s5TxManager() {
        return new DefaultDataSourceTransactionManager(slaver5DataSource());
    }

    @Bean(name = MY_CAT_MANAGER)
    @Conditional(MyCat6DataSourceCondition.class)
    public DefaultDataSourceTransactionManager myCat6TxManager() {
        return new DefaultDataSourceTransactionManager(myCat6DataSource());
    }

    @Bean(name = REWARD_MANAGER)
    @Conditional(S7DataSourceCondition.class)
    public DefaultDataSourceTransactionManager s7TxManager() {
        return new DefaultDataSourceTransactionManager(slaver7DataSource());
    }

    @Bean
    public TransactionDefinitionInterceptor transactionDefinitionInterceptor() {
        return new TransactionDefinitionInterceptor();
    }

    /**
     * @see #transactionDefinitionInterceptor()
     */
    @Bean
    public DefaultPointcutAdvisor transactionDefinitionPointcutAdvisor() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("@annotation(org.springframework.transaction.annotation.Transactional)");

        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, transactionDefinitionInterceptor());
        advisor.setOrder(0);
        return advisor;
    }





}



