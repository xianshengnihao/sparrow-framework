package com.sina.sparrowframework.rocketmq.db.mapper;

import com.sina.sparrowframework.mybatis.internal.IBaseDao;
import com.sina.sparrowframework.rocketmq.db.MessageTransaction;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LocalTransactionMapper extends IBaseDao<MessageTransaction> {
}
