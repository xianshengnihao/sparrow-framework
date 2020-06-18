package com.sina.sparrowframework.sinacloud;

import org.springframework.lang.NonNull;

import java.util.Collections;
import java.util.List;

/**
 * 表示一个目录下一页的结果
 * created  on 2018-12-20.
 */
public class ListResult {

    /**
     * 同 sql 中的 offset,
     * 用对查下页
     */
    @NonNull
    private final String offset;

    /**
     * true 表示没有下一页
     */
    private final boolean end;


    /**
     * 一个目录下的文件路径列表.
     */
    @NonNull
    private final List<String> pathList;


    ListResult(@NonNull String offset, boolean end, @NonNull List<String> pathList) {
        this.offset = offset;
        this.end = end;
        this.pathList = Collections.unmodifiableList(pathList);
    }

    @NonNull
    public String getOffset() {
        return offset;
    }

    public boolean isEnd() {
        return end;
    }

    @NonNull
    public List<String> getPathList() {
        return pathList;
    }
}
