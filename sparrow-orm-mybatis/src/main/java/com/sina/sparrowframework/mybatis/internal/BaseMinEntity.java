package com.sina.sparrowframework.mybatis.internal;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author 基础实体类
 * @date 2019/7/5 15:27
 */
public class BaseMinEntity implements Serializable {

    @TableField(strategy = FieldStrategy.IGNORED,fill = FieldFill.INSERT)
    @TableId(value = "id",type = IdType.INPUT)
    private Long id;

    @TableField(value = "create_time")
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.UPDATE, update = "now()")
    private LocalDateTime updateTime;

    @TableLogic(value = "1", delval = "0")
    private Boolean visible;

    @Version
    @TableField(value="version", fill = FieldFill.UPDATE, update="%s+1")
    private Integer version;

    @TableField(value = "remark")
    private String remark;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
