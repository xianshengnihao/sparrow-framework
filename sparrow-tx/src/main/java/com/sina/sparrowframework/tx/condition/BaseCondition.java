package com.sina.sparrowframework.tx.condition;

import com.sina.sparrowframework.tx.helper.Constants;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.List;


/**
 * <p>
 *
 * </p>
 * @date 2019/11/20 13:59
 */
abstract public class BaseCondition{
    private static final String PROJECT_SUPPORTED_MULTIPLE_DATASOURCE = "project.supported.multiple.datasource";

    /**
     * 校验是否支持数据源
     *
     * @param environment
     * @param ds
     * @return
     */
    protected Boolean isSupportDs(Environment environment, String ds) {
        List dsLst = environment.getProperty(PROJECT_SUPPORTED_MULTIPLE_DATASOURCE, List.class, Arrays.asList(new String[]{Constants.s0}));
        if (dsLst.contains(ds)) {
            return true;
        }
        return false;
    }
}
