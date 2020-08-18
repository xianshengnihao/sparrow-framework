package com.sina.sparrowframework.mybatis.internal;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.sina.sparrowframework.id.conf.IdProviderConfig;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

/**
 * <p>id自动赋值</p>
 */
@Component("idMetaObjectHandler")
public class IdMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        Object id = this.getFieldValByName("id", metaObject);
        if (id == null) {
            this.setFieldValByName("id", IdProviderConfig.id.genId(), metaObject);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {

    }
}
