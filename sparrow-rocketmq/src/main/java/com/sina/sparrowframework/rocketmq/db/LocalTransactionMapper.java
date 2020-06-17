package com.sina.sparrowframework.rocketmq.db;

import com.sina.sparrowframework.mybatis.internal.IBaseDao;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LocalTransactionMapper extends IBaseDao<MessageTransaction> {
}
