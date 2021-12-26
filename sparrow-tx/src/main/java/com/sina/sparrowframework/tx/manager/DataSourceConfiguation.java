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
    public void afterPropertiesSet() throws Exception {
    }

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
     * 编号为 8 的从库(dwc项目从库)
     * 动态数据源
     */
    @Bean(name = "slaver8DataSource", initMethod = "init", destroyMethod = "close")
    @Conditional(S8DataSourceCondition.class)
    public DruidDataSource slaver8DataSource() {
        return createMasterDataSource(false, 8);
    }

    /**
     * 编号为 9 的从库(bigdata tidb)
     * 动态数据源
     */
    @Bean(name = "slaver9DataSource", initMethod = "init", destroyMethod = "close")
    @Conditional(S9DataSourceCondition.class)
    public DruidDataSource slaver9DataSource() {
        return createMasterDataSource(false, 9);
    }

    /**
     * 编号为 10 的从库(kangaroo)
     * 动态数据源
     */
    @Bean(name = "slaver10DataSource", initMethod = "init", destroyMethod = "close")
    @Conditional(S10DataSourceCondition.class)
    public DruidDataSource slaver10DataSource() {
        return createMasterDataSource(false, 10);
    }

    /**
     * 编号为 11 的从库(giraffe)
     * 动态数据源
     */
    @Bean(name = "slaver11DataSource", initMethod = "init", destroyMethod = "close")
    @Conditional(S11DataSourceCondition.class)
    public DruidDataSource slaver11DataSource() {
        return createMasterDataSource(false, 11);
    }

    /**
     * 编号为 12 的从库(koala)
     * 动态数据源
     */
    @Bean(name = "slaver12DataSource", initMethod = "init", destroyMethod = "close")
    @Conditional(S12DataSourceCondition.class)
    public DruidDataSource slaver12DataSource() {
        return createMasterDataSource(false, 12);
    }

    /**
     * 编号为 13 的从库(sm)
     * 动态数据源
     */
    @Bean(name = "slaver13DataSource", initMethod = "init", destroyMethod = "close")
    @Conditional(S13DataSourceCondition.class)
    public DruidDataSource slaver13DataSource() {
        return createMasterDataSource(false, 13);
    }

    /**
     * 编号为 14 的从库(finance_app_users)
     * 动态数据源
     */
    @Bean(name = "slaver14DataSource", initMethod = "init", destroyMethod = "close")
    @Conditional(S14DataSourceCondition.class)
    public DruidDataSource slaver14DataSource() {
        return createMasterDataSource(false, 14);
    }

    /**
     * 编号为 15 的从库(finance_message)
     * 动态数据源
     */
    @Bean(name = "slaver15DataSource", initMethod = "init", destroyMethod = "close")
    @Conditional(S15DataSourceCondition.class)
    public DruidDataSource slaver15DataSource() {
        return createMasterDataSource(false, 15);
    }

    /**
     * 编号为 16 的从库(finance_message_line)
     * 动态数据源
     */
    @Bean(name = "slaver16DataSource", initMethod = "init", destroyMethod = "close")
    @Conditional(S16DataSourceCondition.class)
    public DruidDataSource slaver16DataSource() {
        return createMasterDataSource(false, 16);
    }

    /**
     * 编号为 17 的从库(finance)
     * 动态数据源
     */
    @Bean(name = "slaver17DataSource", initMethod = "init", destroyMethod = "close")
    @Conditional(S17DataSourceCondition.class)
    public DruidDataSource slaver17DataSource() {
        return createMasterDataSource(false, 17);
    }

    /**
     * 编号为 18 的从库(finance_contact_info)
     * 动态数据源
     */
    @Bean(name = "slaver18DataSource", initMethod = "init", destroyMethod = "close")
    @Conditional(S18DataSourceCondition.class)
    public DruidDataSource slaver18DataSource() {
        return createMasterDataSource(false, 18);
    }

    /**
     * 编号为 19 的从库(finance_device_info)
     * 动态数据源
     */
    @Bean(name = "slaver19DataSource", initMethod = "init", destroyMethod = "close")
    @Conditional(S19DataSourceCondition.class)
    public DruidDataSource slaver19DataSource() {
        return createMasterDataSource(false, 19);
    }

    /**
     * 编号为 20 的从库(finance_insurance)
     * 动态数据源
     */
    @Bean(name = "slaver20DataSource", initMethod = "init", destroyMethod = "close")
    @Conditional(S20DataSourceCondition.class)
    public DruidDataSource slaver20DataSource() {
        return createMasterDataSource(false, 20);
    }

    /**
     * 编号为 21 的从库(积分商城-finance_pointsmall)
     * 动态数据源
     */
    @Bean(name = "slaver21DataSource", initMethod = "init", destroyMethod = "close")
    @Conditional(S21DataSourceCondition.class)
    public DruidDataSource slaver21DataSource() {
        return createMasterDataSource(false, 21);
    }

    /**
     * 编号为 22 的从库(xincai_trade)
     * 动态数据源
     */
    @Bean(name = "slaver22DataSource", initMethod = "init", destroyMethod = "close")
    @Conditional(S22DataSourceCondition.class)
    public DruidDataSource slaver22DataSource() {
        return createMasterDataSource(false, 22);
    }

    /**
     * 编号为 23 的从库(koala-manager)
     * 动态数据源
     */
    @Bean(name = "slaver23DataSource", initMethod = "init", destroyMethod = "close")
    @Conditional(S23DataSourceCondition.class)
    public DruidDataSource slaver23DataSource() {
        return createMasterDataSource(false, 23);
    }

    /**
     * 编号为 23 的从库(scale)
     * 动态数据源
     */
    @Bean(name = "slaver24DataSource", initMethod = "init", destroyMethod = "close")
    @Conditional(S24DataSourceCondition.class)
    public DruidDataSource slaver24DataSource() {
        return createMasterDataSource(false, 24);
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
        if (isSupportDs(env, Constants.s8)) {
            DataSource s8 = slaver8DataSource();
            if (!ObjectToolkit.isEmpty(s8)) {
                dsMap.put(Constants.s8, s8);
            }
        }
        if (isSupportDs(env, Constants.s9)) {
            DataSource s9 = slaver9DataSource();
            if (!ObjectToolkit.isEmpty(s9)) {
                dsMap.put(Constants.s9, s9);
            }
        }
        if (isSupportDs(env, Constants.s10)) {
            DataSource s10 = slaver10DataSource();
            if (!ObjectToolkit.isEmpty(s10)) {
                dsMap.put(Constants.s10, s10);
            }
        }
        if (isSupportDs(env, Constants.s11)) {
            DataSource s11 = slaver11DataSource();
            if (!ObjectToolkit.isEmpty(s11)) {
                dsMap.put(Constants.s11, s11);
            }
        }
        if (isSupportDs(env, Constants.s12)) {
            DataSource s12 = slaver12DataSource();
            if (!ObjectToolkit.isEmpty(s12)) {
                dsMap.put(Constants.s12, s12);
            }
        }
        if (isSupportDs(env, Constants.s13)) {
            DataSource s13 = slaver13DataSource();
            if (!ObjectToolkit.isEmpty(s13)) {
                dsMap.put(Constants.s13, s13);
            }
        }
        if (isSupportDs(env, Constants.s14)) {
            DataSource s14 = slaver14DataSource();
            if (!ObjectToolkit.isEmpty(s14)) {
                dsMap.put(Constants.s14, s14);
            }
        }
        if (isSupportDs(env, Constants.s15)) {
            DataSource s15 = slaver15DataSource();
            if (!ObjectToolkit.isEmpty(s15)) {
                dsMap.put(Constants.s15, s15);
            }
        }
        if (isSupportDs(env, Constants.s16)) {
            DataSource s16 = slaver16DataSource();
            if (!ObjectToolkit.isEmpty(s16)) {
                dsMap.put(Constants.s16, s16);
            }
        }
        if (isSupportDs(env, Constants.s17)) {
            DataSource s17 = slaver17DataSource();
            if (!ObjectToolkit.isEmpty(s17)) {
                dsMap.put(Constants.s17, s17);
            }
        }
        if (isSupportDs(env, Constants.s18)) {
            DataSource s18 = slaver18DataSource();
            if (!ObjectToolkit.isEmpty(s18)) {
                dsMap.put(Constants.s18, s18);
            }
        }
        if (isSupportDs(env, Constants.s19)) {
            DataSource s19 = slaver19DataSource();
            if (!ObjectToolkit.isEmpty(s19)) {
                dsMap.put(Constants.s19, s19);
            }
        }
        if (isSupportDs(env, Constants.s20)) {
            DataSource s20 = slaver20DataSource();
            if (!ObjectToolkit.isEmpty(s20)) {
                dsMap.put(Constants.s20, s20);
            }
        }
        if (isSupportDs(env, Constants.s21)) {
            DataSource s21 = slaver21DataSource();
            if (!ObjectToolkit.isEmpty(s21)) {
                dsMap.put(Constants.s21, s21);
            }
        }
        if (isSupportDs(env, Constants.s22)) {
            DataSource s22 = slaver22DataSource();
            if (!ObjectToolkit.isEmpty(s22)) {
                dsMap.put(Constants.s22, s22);
            }
        }
        if (isSupportDs(env, Constants.s23)) {
            DataSource s23 = slaver23DataSource();
            if (!ObjectToolkit.isEmpty(s23)) {
                dsMap.put(Constants.s23, s23);
            }
        }
        if (isSupportDs(env, Constants.s24)) {
            DataSource s24 = slaver24DataSource();
            if (!ObjectToolkit.isEmpty(s24)) {
                dsMap.put(Constants.s24, s24);
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

    @Bean(name = REWARD_TX_MANAGER)
    @Conditional(S7DataSourceCondition.class)
    public DefaultDataSourceTransactionManager s7TxManager() {
        return new DefaultDataSourceTransactionManager(slaver7DataSource());
    }

    @Bean(name = DWC_TX_MANAGER)
    @Conditional(S8DataSourceCondition.class)
    public DefaultDataSourceTransactionManager s8TxManager() {
        return new DefaultDataSourceTransactionManager(slaver8DataSource());
    }

    @Bean(name = BIG_DATA_TX_MANAGER)
    @Conditional(S9DataSourceCondition.class)
    public DefaultDataSourceTransactionManager s9TxManager() {
        return new DefaultDataSourceTransactionManager(slaver9DataSource());
    }

    @Bean(name = KANGAROO_TX_MANAGER)
    @Conditional(S10DataSourceCondition.class)
    public DefaultDataSourceTransactionManager s10TxManager() {
        return new DefaultDataSourceTransactionManager(slaver10DataSource());
    }

    @Bean(name = GIRAFFE_TX_MANAGER)
    @Conditional(S11DataSourceCondition.class)
    public DefaultDataSourceTransactionManager s11TxManager() {
        return new DefaultDataSourceTransactionManager(slaver11DataSource());
    }

    @Bean(name = KOALA_TX_MANAGER)
    @Conditional(S12DataSourceCondition.class)
    public DefaultDataSourceTransactionManager s12TxManager() {
        return new DefaultDataSourceTransactionManager(slaver12DataSource());
    }

    @Bean(name = SM_TX_MANAGER)
    @Conditional(S13DataSourceCondition.class)
    public DefaultDataSourceTransactionManager s13TxManager() {
        return new DefaultDataSourceTransactionManager(slaver13DataSource());
    }

    @Bean(name = FINANCE_USER_TX_MANAGER)
    @Conditional(S14DataSourceCondition.class)
    public DefaultDataSourceTransactionManager s14TxManager() {
        return new DefaultDataSourceTransactionManager(slaver14DataSource());
    }

    @Bean(name = FINANCE_USER_MSG_TX_MANAGER)
    @Conditional(S15DataSourceCondition.class)
    public DefaultDataSourceTransactionManager s15TxManager() {
        return new DefaultDataSourceTransactionManager(slaver15DataSource());
    }

    @Bean(name = FINANCE_MSG_TX_MANAGER)
    @Conditional(S16DataSourceCondition.class)
    public DefaultDataSourceTransactionManager s16TxManager() {
        return new DefaultDataSourceTransactionManager(slaver16DataSource());
    }

    @Bean(name = FINANCE_PLATFORM_TX_MANAGER)
    @Conditional(S17DataSourceCondition.class)
    public DefaultDataSourceTransactionManager s17TxManager() {
        return new DefaultDataSourceTransactionManager(slaver17DataSource());
    }

    @Bean(name = FINANCE_CONTACT_INFO_TX_MANAGER)
    @Conditional(S18DataSourceCondition.class)
    public DefaultDataSourceTransactionManager s18TxManager() {
        return new DefaultDataSourceTransactionManager(slaver18DataSource());
    }

    @Bean(name = FINANCE_DEVICE_INFO_TX_MANAGER)
    @Conditional(S19DataSourceCondition.class)
    public DefaultDataSourceTransactionManager s19TxManager() {
        return new DefaultDataSourceTransactionManager(slaver19DataSource());
    }

    @Bean(name = FINANCE_INSURANCE_TX_MANAGER)
    @Conditional(S20DataSourceCondition.class)
    public DefaultDataSourceTransactionManager s20TxManager() {
        return new DefaultDataSourceTransactionManager(slaver20DataSource());
    }

    @Bean(name = FINANCE_POINTS_MALL_TX_MANAGER)
    @Conditional(S21DataSourceCondition.class)
    public DefaultDataSourceTransactionManager s21TxManager() {
        return new DefaultDataSourceTransactionManager(slaver21DataSource());
    }

    @Bean(name = XIN_CAI_TRADE_TX_MANAGER)
    @Conditional(S22DataSourceCondition.class)
    public DefaultDataSourceTransactionManager s22TxManager() {
        return new DefaultDataSourceTransactionManager(slaver22DataSource());
    }

    @Bean(name = KOALA_MGR_TX_MANAGER)
    @Conditional(S23DataSourceCondition.class)
    public DefaultDataSourceTransactionManager s23TxManager() {
        return new DefaultDataSourceTransactionManager(slaver23DataSource());
    }

    @Bean(name = SCALE_TX_MANAGER)
    @Conditional(S24DataSourceCondition.class)
    public DefaultDataSourceTransactionManager s24TxManager() {
        return new DefaultDataSourceTransactionManager(slaver24DataSource());
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



