package com.sina.sparrow.rocketmq.db;


import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
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

}
