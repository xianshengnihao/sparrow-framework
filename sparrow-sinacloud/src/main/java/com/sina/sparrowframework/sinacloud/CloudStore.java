package com.sina.sparrowframework.sinacloud;

import com.sina.sparrowframework.exception.business.DataException;
import org.springframework.lang.NonNull;

/**
 * created  on 2018/10/12.
 */
public interface CloudStore {


    /**
     * 上传到云上,并将访问权限设置为 公共,访问时默认允许网络节点进行缓存.
     *
     * @throws CloudStoreException 上传出错
     */
    CloudMeta upload(StoreForm form) throws CloudStoreException, DataException;


    /**
     * @param path 在云存储上的路径, 不以 {@code /} 开头
     * @throws CloudStoreException 下载出错
     */
    DownloadResult download(String path) throws CloudStoreException, DataException;

    /**
     * @param path 在云存储上的路径, 不以 {@code /} 开头
     */
    boolean exists(String path) throws CloudStoreException, DataException;

    /**
     * @param path 在云存储上的路径, 不以 {@code /} 开头
     * @return null or 元数据
     */
    CloudMeta meta(String path) throws CloudStoreException, DataException;


    /**
     * @param path 在云存储上的路径, 不以 {@code /} 开头
     * @return null or http 绝对路径
     */
    String getUrl(String path) throws CloudStoreException, DataException;

    /**
     * 分页列出一个前缀下的文件路径.
     *
     * @param prefix   文件前缀,类似于目录.
     * @param offset   类似于 sql 中的 offset 用于查下一页,第一页是为 null
     * @param rowCount 一页的最大行数,不能超过 500
     * @return 列出的路径.
     */
    @NonNull
    ListResult listPath(String prefix, String offset, int rowCount) throws CloudStoreException, DataException;

    /**
     * 对url进行decode
     *
     * @param url       要处理的url
     * @param express   该url的过期时间
     * @return url
     */
    String wrapUrl(String url, Long express) throws CloudStoreException, DataException;


}
