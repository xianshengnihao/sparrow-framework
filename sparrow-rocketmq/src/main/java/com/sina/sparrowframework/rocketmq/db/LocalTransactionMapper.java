package com.sina.sparrowframework.rocketmq.db;

import com.sina.sparrowframework.mybatis.internal.IBaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

//@Mapper
@Component
public interface LocalTransactionMapper extends IBaseDao<MessageTransaction> {
}
