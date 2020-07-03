package com.sina.sparrowframework.metadata.constants;


@SuppressWarnings("unused")
public enum BaseCode implements CodeManager {

    SUCCESS("000000", "成功"),
    BAD_REQUEST("001010", "参数校验错误"),
    INVALID_SIGNATURE("001020", "客户端签名无效"),
    ASSERT_ERROR("001030", "断言错误(自定义描述)"),
    NETWORK_ERROR("001040", "网络异常"),
    API_SIGN_ERROR("001050", "Api验签失败"),
    THIRD_REMOTE_ERROR("001070", "调用第三方错误"),
    MAINTENANCE_PERIOD_CHECK_ERROR("001080", "维护期间反馈异常code"),
    TRY_AGAIN_LATER_ERROR("001090", "请稍后再试"),
    NOT_FOUND("001110", "未找到资源"),
    METHOD_NOT_SUPPORTED("001120", "不支持的方法"),
    MEDIA_TYPE_NOT_ACCEPTABLE("001130", "请求资源不可访问"),
    HTTP_MEDIA_TYPE_NOT_SUPPORTED("001140", "不支持的媒体类型"),
    UNKNOWN_ERROR("999999", "未知错误");

    private final String code;
    private final String desc;

    BaseCode(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getName() {
        return this.name();
    }

    @Override
    public String getDesc() {
        return desc;
    }

}
