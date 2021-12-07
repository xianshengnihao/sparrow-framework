package com.sina.sparrowframework.tx.condition;

import com.sina.sparrowframework.tx.helper.Constants;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * finance_contact_info数据源开启条件
 *
 * @author tianye6
 * @date 2021/11/25 14:07
 */
public class S18DataSourceCondition extends BaseCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        if (isSupportDs(context.getEnvironment(), Constants.s18)) {
            return true;
        }
        return false;
    }

}
