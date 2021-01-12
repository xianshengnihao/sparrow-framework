package com.sina.sparrowframework.constants;

import com.sina.sparrowframework.tools.utility.StrPool;

/**
 * 全局公共常量类属性
 */
public interface GlobalConstant extends StrPool {

    /**
     * 理财系统用户唯一编号
     **/
    String GLOBAL_USER_ID = "userId";

    /**
     * 理财系统授权唯一编号
     **/
    String GLOBAL_OPEN_ID = "openId";

    /**
     * 理财系统合作渠道编号
     **/
    String CHANNEL_CODE_KEY = "channelCode";

    /**
     * 鉴权请求头名称
     */
    String AUTH_HEADER_KEY = "sparrow-authorization";

    /**
     * h5拦截路径前缀
     */
    String H5_INTERCEPTOR_PATH_PREFIX = "global.H5.interceptor.path.prefix";

    /**
     * 路由过滤url
     */
    String GATEWAY_FILTER_URL = "global.auth.filterUrl";

    /**
     * aes密钥key
     */
    String GLOBAL_AES_PIRVATE_KEY = "global.aes.private.key";

    /**
     * jwt密钥key
     */
    String GLOBAL_JWT_PIRVATE_KEY = "global.jwt.private.key";

    /**
     * 下划线分隔符
     **/
    String UNDERLINE_SEPARATOR = "_";

    /**
     * 逗号分隔符
     **/
    String COMMA_SEPARATOR = ",";

    /**
     * 分号分隔符
     **/
    String SEMICOLON_SEPARATOR = ";";

    /**
     * 斜杠分隔符
     **/
    String SLASH_SEPARATOR = "/";

    /**
     * 冒号分隔符
     **/
    String COLON_SEPARATOR = ":";

    /**
     * 或分隔符
     **/
    String OR_SEPARATOR = " ||";

    /**
     * encode加号
     */
    String ENCODE_PLUS = "%2B";

    /**
     * 分页起始默认当前页
     **/
    Long GLOBAL_PAGE_NUM = 1L;
    /**
     * 分页起始默认当页数
     **/
    Long GLOBAL_PAGE_SIZE = 1000L;
    /**
     * 邮件开关
     */
    String MAIL_MASTER_SWITCH = "mail.master.switch";
    /**
     * [项目启动]接收人
     */
    String PROJECT_MAIL_NOTICE_TO = "project.startup.to";
    /**
     * [项目启动]主题
     */
    String PROJECT_MAIL_NOTICE_SUBJECT = "project.startup.subject";
    /**
     * [项目启动]抄送
     */
    String PROJECT_MAIL_NOTICE_CC = "project.subject.cc";


    /**
     * 项目启动邮件通知
     */
    String PROJECT_STARTUP_NOTICE_SWITCH = "project.startup.notice.switch";
    /**
     * 警告邮件开关
     */
    String WARN_MAIL_MASTER_SWITCH = "warn.mail.master.switch";
    /**
     * 邮件警告通知接收人
     */
    String WARN_MAIL_NOTICE_TO = "warn.mail.notice.to";
    /**
     * 邮件警告通知主题
     */
    String WARN_MAIL_NOTICE_SUBJECT = "warn.mail.notice.subject";
    /**
     * 邮件警告通知抄送人
     */
    String WARN_MAIL_NOTICE_CC = "warn.mail.notice.cc";
    /**
     * 邮件警告模板全路径
     */
    String WARN_MAIL_TEMPLATE_FULL_PATH = "warn.mail.notice.template.full.path";

    /**
     * 微博accessToken的url
     */
    String WB_ACCESS_TOKEN_URL = "weibo.lightapp.access_token.url";

    /**
     * 微博accessToken有效期
     */
    String WB_ACCESS_TOKEN_EXPIRE_TIME = "weibo.lightapp.access_token.expire.time";

    /**
     * 微博appKe
     */
    String WB_APP_KEY = "weibo.lightapp.appkey";

    /**
     * 微博白名单IP
     */
    String WB_WHITE_LIST_IPS = "weibo.lightapp.whiteList.ips";

    /**
     * 理财平台对应微博accessToken的redisKey
     */
    String LCPT_SSO_ACCESS_TOKEN = "lcpt:sso:accessToken";

    /**
     * 微博用户信息url
     */
    String WB_USER_BASE_INFO_URL = "weibo.lightapp.baseinfo.url";

    /**
     * 项目支持对账数据源
     */
    String PROJECT_SUPPORTED_MULTIPLE_DATASOURCE = "project.supported.multiple.datasource";

    /**
     * 图片base64字符串前缀
     */
    String IMAGE_BASE64_SUFFIX = "data:image/jpeg;base64,";

    /**
     * 特定渠道维护开关,true：启用，false:禁用
     */
    String CHANNEL_MAINTAIN_SWITCH_KEY = "%s.maintain.switch.key";

    /**
     * 特定渠道的业务维护期起始时间
     */
    String CHANNEL_MAINTAIN_START_DATETIME = "%s.maintenance.period.start.datetime";

    /**
     * 特定渠道的业务维护期结束时间
     */
    String CHANNEL_MAINTAIN_END_DATETIME = "%s.maintenance.period.end.datetime";

    /**
     * 特定渠道的业务维护期起始时间
     */
    String CHANNEL_URI_MAINTAIN_START_DATETIME = "%s.[%s].maintenance.period.start.datetime";

    /**
     * 特定渠道的业务维护期结束时间
     */
    String CHANNEL_URI_MAINTAIN_END_DATETIME = "%s.[%s].maintenance.period.end.datetime";

    /**
     * 特定渠道维护期提示描述
     */
    String CHANNEL_MAINTENANCE_PERIOD_DISPLAY = "%s.maintenance.period.display";

    /**
     * 特定渠道特定业务维护期提示描述
     */
    String CHANNEL_URI_MAINTENANCE_PERIOD_DISPLAY = "%s.[%s].maintenance.period.display";

    /**
     * 特定渠道维护url
     */
    String CHANNEL_MAINTAIN_FILTER_URI_LIST = "%s.maintain.filter.uri.list";

    /**
     * h5端请求日志过滤
     */
    String ROC_LOG_FILTER_URI_LIST = "roc.log.filter.uri.list";

    //scan package
    String SP_SPARROWFRAMEWORK = "com.sina.sparrowframework";

    String SP_SPARROW = "com.sina.sparrow";

    String SP_BASE = "com.sina";

    String SP_BASE_MAPPER = "com.sina.sparrowframework.rocketmq.db.mapper";
}
