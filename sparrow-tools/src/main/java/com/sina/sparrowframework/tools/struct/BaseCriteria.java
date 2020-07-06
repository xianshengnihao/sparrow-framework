package com.sina.sparrowframework.tools.struct;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;

import static com.sina.sparrowframework.tools.utility.DateUtil.DATE_TIME_FORMAT;

/**
 * created  on 2018/4/18.
 */
public class BaseCriteria implements Serializable {

    private static final long serialVersionUID = 2409449906669029202L;

    /**
     * true 表示需要返回总行数
     */
    private Boolean queryRowCount;

    /**
     * true 表示 时间正序,false 表示时间倒序.
     * 下层默认是按时间倒序
     */
    private Boolean ascOrder;

    @DateTimeFormat(pattern = DATE_TIME_FORMAT)
    @JsonFormat(pattern = DATE_TIME_FORMAT)
    private LocalDateTime startCreateTime;

    @DateTimeFormat(pattern = DATE_TIME_FORMAT)
    @JsonFormat(pattern = DATE_TIME_FORMAT)
    private LocalDateTime endCreateTime;

    /**
     * 分页查询中上一页的最后一行 id
     */
    private Long lastId;

    /**
     * 分页查询中上一页的首行 id
     */
    private Long firstId;

    private BigInteger bigLastId;

    /**
     * 在 getter 中设置了默认值,否则会在 dao 中查出错
     */
    @Min(0)
    private Integer offset;

    /**
     * 在 getter 中设置了默认值,否则会在 dao 中查出错
     */
    @Min(1)
    @Max(1000)
    private Integer rowCount;

    /**
     * 在 getter 中设置了默认值,否则会在 dao 中查出错
     */
    public Integer getOffset() {
        if (offset == null) {
            offset = 0;
        }
        return offset;
    }

    public BaseCriteria setOffset(Integer offset) {
        this.offset = offset;
        return this;
    }

    /**
     * 在 getter 中设置了默认值,否则会在 dao 中查出错
     */
    public Integer getRowCount() {
        if (rowCount == null) {
            rowCount = 10;
        }
        return rowCount;
    }

    public BaseCriteria setRowCount(Integer rowCount) {
        this.rowCount = rowCount;
        return this;
    }

    public Boolean getQueryRowCount() {
        if (queryRowCount == null) {
            queryRowCount = Boolean.FALSE;
        }
        return queryRowCount;
    }

    public BaseCriteria setQueryRowCount(Boolean queryRowCount) {
        this.queryRowCount = queryRowCount;
        return this;
    }

    public Boolean getAscOrder() {
        if (ascOrder == null) {
            ascOrder = Boolean.FALSE;
        }
        return ascOrder;
    }

    public BaseCriteria setAscOrder(Boolean ascOrder) {
        this.ascOrder = ascOrder;
        return this;
    }

    public LocalDateTime getStartCreateTime() {
        return startCreateTime;
    }

    public BaseCriteria setStartCreateTime(LocalDateTime startCreateTime) {
        this.startCreateTime = startCreateTime;
        return this;
    }

    public LocalDateTime getEndCreateTime() {
        return endCreateTime;
    }

    public BaseCriteria setEndCreateTime(LocalDateTime endCreateTime) {
        this.endCreateTime = endCreateTime;
        return this;
    }

    public Long getLastId() {
        return lastId;
    }

    public BaseCriteria setLastId(Long lastId) {
        this.lastId = lastId;
        return this;
    }

    public BigInteger getBigLastId() {
        return bigLastId;
    }

    public BaseCriteria setBigLastId(BigInteger bigLastId) {
        this.bigLastId = bigLastId;
        return this;
    }

    public Long getFirstId() {
        return firstId;
    }

    public BaseCriteria setFirstId(Long firstId) {
        this.firstId = firstId;
        return this;
    }
}
