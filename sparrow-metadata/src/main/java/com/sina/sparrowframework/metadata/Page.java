package com.sina.sparrowframework.metadata;

import java.util.List;

/**
 * 页
 */
public class Page<T> {
    /**
     * 总记录数
     */
    private Integer totalCount;
    /**
     * 每页记录数
     */
    private Integer pageSize;
    /**
     * 总页数
     */
    private Integer totalPage;
    /**
     * 当前页数
     */
    private Integer pageNum;
    /**
     * 列表数据
     */
    private List<T> records;

    public Page(List<T> records, Integer totalCount, Integer pageSize, Integer pageNum) {
        this.records = records;
        this.totalCount = totalCount;
        this.pageSize = pageSize;
        this.pageNum = pageNum;
        this.totalPage = (int) Math.ceil((double) totalCount / pageSize);
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

}
