package com.sina.sparrowframework.metadata.constants;

/**
 * Created by wxn on 2019-06-28
 */
@SuppressWarnings("unused")
public interface CodeManager {

    String getCode();
    String getDesc();

    interface SystemCode {
        /**
         * 账户系统
         */
        String ROC = "01";
        /**
         * 产品系统
         */
        String PANGOLIN = "02";
        /**
         * 交易系统
         */
        String DRAGON = "03";
        /**
         * 理财平台系统
         */
        String SPARROW_MANAGER = "04";

        /**
         * 酒证系统
         */
        String DWC = "06";

        /**
         * 运营系统
         */
        String KANGAROO = "07";
    }

}
