package com.sina.sparrow.rocketmq.db;

import com.sina.sparrow.core.base.IBaseDao;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LocalTransactionMapper extends IBaseDao<MessageTransaction> {
}
