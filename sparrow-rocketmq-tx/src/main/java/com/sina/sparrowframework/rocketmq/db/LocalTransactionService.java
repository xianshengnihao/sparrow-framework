package com.sina.sparrow.rocketmq.db;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sina.sparrow.core.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service("localTransactionService")
public class LocalTransactionService extends BaseServiceImpl<LocalTransactionMapper, MessageTransaction>{

    @Transactional(transactionManager = TX_MANAGER, rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void log(MessageTransaction localTransactionEntity){
        this.baseMapper.insert(localTransactionEntity);
    }


    @Transactional(transactionManager = TX_MANAGER, rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public boolean exist(String transactionId){
        LambdaQueryWrapper<MessageTransaction> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(MessageTransaction::getTransactionId, transactionId);
        MessageTransaction entity = baseMapper.selectOne(wrapper);
        return entity != null;
    }

}
