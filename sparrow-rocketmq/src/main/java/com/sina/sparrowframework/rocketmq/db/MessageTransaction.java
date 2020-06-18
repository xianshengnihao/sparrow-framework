package com.sina.sparrowframework.rocketmq.db;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@TableName("msg_transaction")
public class MessageTransaction implements Serializable {
    /**
     * CREATE TABLE `msg_transaction` (
     *   `id` bigint(20) NOT NULL DEFAULT '0' COMMENT '主键 id',
     *   `transaction_id` varchar(64) NOT NULL DEFAULT '' COMMENT '事务id',
     *   `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
     *   PRIMARY KEY (`id`),
     *   KEY `idx_transaction_id` (`transaction_id`)
     * ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='事务回查';
     */

    @TableField(strategy = FieldStrategy.IGNORED,fill = FieldFill.INSERT)
    @TableId(value = "id",type = IdType.INPUT)
    private Long id;


    @TableField(value = "transaction_id")
    private String transactionId;

    @TableField(value = "create_time")
    private LocalDateTime create_time;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public MessageTransaction setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public LocalDateTime getCreate_time() {
        return create_time;
    }

    public void setCreate_time(LocalDateTime create_time) {
        this.create_time = create_time;
    }
}
