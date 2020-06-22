package com.sina.sparrowframework.metadata.constants;

/**
 * Created by wxn on 2019-06-28
 */
public interface CodeManager {


    String SUCCESS = "000000";
    /**
     * 参数校验错误
     */
    String BAD_REQUEST = "001010";
    /**
     * 客户端签名无效
     *
     * @return
     */
    String INVALID_SIGNATURE = "001020";

    /**
     * 断言错误(自定义描述)
     */
    String ASSERT_ERROR = "001030";

    /**
     * 网络异常
     */
    String NETWORK_ERROR = "001040";

    /**
     * Api验签失败
     */
    String API_SIGN_ERROR = "001050";

    /**
     * 使用新浪云存储出现错误
     */
    String SINA_CLOUND_STORE_ERROR = "001060";

    /**
     * 调用第三方错误
     */
    String THIRD_REMOTE_ERROR = "001070";

    /**
     * 维护期间反馈异常code
     */
    String MAINTENANCE_PERIOD_CHECK_ERROR = "001080";

    /**
     * 请稍后再试
     */
    String TRY_AGAIN_LATER_ERROR = "001090";

    /**
     * 不知错误
     */
    String UNKNOWN_ERROR = "999999";


    public interface SystemCode {
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
    }


}
