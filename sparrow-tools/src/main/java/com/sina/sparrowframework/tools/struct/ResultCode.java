package com.sina.sparrowframework.tools.struct;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * created  on 02/03/2018.
 * @deprecated use {@link com.sina.sparrowframework.metadata.constants.CodeManager}
 */
@SuppressWarnings( "unused" )
@Deprecated
public enum ResultCode implements CodeEnum {

    ok(200, "成功",null),


    /* 常见的客户端错误 */
    clientError(400, "客户端错误",null),
    notFound(404, "未找到处理器",clientError),
    methodNotAllowed(405, "方法不允许",clientError),
    notAcceptable(406, "不能生成客户端需要的媒介",clientError),

    unsupportedMedia(415, "媒介类型不被支持",clientError),
    requestBodyError(418, "请求body错误",clientError),
    dataExists(419, "数据已存在",clientError),
    dataError(420, "数据错误",clientError),

    dataValidateError(421, "数据校验错误",clientError),
    invalidSignature(422, "客户端签名无效",clientError),
    uploadError(423, "客户端上传错误",clientError),
    //客户端在我方服务器上没有存储其公钥,这种情况是非法访问
    notFoundPublicKey(424,"未找到客户端的公钥",clientError),
    requestNoRepeat(425,"请求流水号重复",clientError),

    /* 服务端错误 */
    serverError(500, "服务端错误",null),
    serviceUnavailable(503, "过载或down机",serverError),
    timeout(504, "服务超时",serverError),
    dataAccessError(510, "数据访问错误",serverError),

    responseBodyError(511, "响应body出错",serverError),
    signatureError(512,"生成签名错误",serverError),
    //服务端生成响应报文时,没有找到相应的私钥为签名响应消息,则返回此码
    notFoundPrivateKey(513,"未找到与客户端匹配的服务端私钥",serverError),
    optimisticLock(514,"乐观锁失败",serverError),

    /*############################ 网络调用(调用第三方)[800-899] start #############################################*/
    noResponse(800, "服务提供方无响应消息",null),
    responseMsgError(810,"服务提供方响应消息有误",noResponse),
    /*############################ 网络调用(调用第三方)[800-899] end #############################################*/

    /*############################ 邮件状态类[900-999] start #############################################*/
    emailSendFailure(900, "邮件发送失败",null),
    /*############################ 邮件状态类[900-999] end #############################################*/

    /*############################ 业务状态类[1000-1499](如:业务锁定,不可用等,具体的业务 code 不要放在这里 .) start #############################################*/
    businessLocked(1000, "业务维护中,请期待",null),

    /*############################ 业务状态类[1000-1499](如:业务锁定,不可用等,具体的业务 code 不要放在这里 .) end #############################################*/


    /*----------------------------- 账户类状态类[1500-2999](如:账户锁定) end ----------------------------------------------*/

    noAccount(1500, "用户未开户",null),
    noTradePwd(1501, "请先设置交易密码",noAccount),
    accountLocked(1502, "您的账户已锁定",noAccount),
    noUsableBankCard(1503, "您已解绑银行卡",noAccount),
    tradePwdNotMatch(1504, "交易密码不匹配",noAccount),
    userMinor(1505, "根据相关法规，不允许未成年投资理财。",noAccount),
    accountFreeze(1506, "您的账户存在风险，已冻结",noAccount),

    restrictWithdraw(1507, "您的提现受限",noAccount),
    balanceLack(1508, "余额不足",noAccount),
    withdrawUpper(1509, "提现超过上限",noAccount),
    withdrawLower(1510, "提现超过下限",noAccount),

    withdrawCountUpper(1511, "提现次数超过上限",noAccount),


    hadOpenAccount(1512, "用户已开户",noAccount),
    identitiesCardHadExist(1513, "开户证件信息已存在",noAccount),
    notCheckBankcard(1514, "未找到银行卡信息",noAccount),
    failCheckSign(1515, "验签失败",noAccount),
    signError(1516, "签名失败",noAccount),

    openAccountFail(1517, "开户失败",noAccount),
    bankcardUnbinded(1518, "未找到有效银行卡",noAccount),
    unauthorized(1519, "该用户未授权给系统",noAccount),
    usernotexist(1520 , "未找到用户信息" ,noAccount),
    accountnotexist(1521 , "未找到账户信息" ,noAccount),

    userAuthenticationExpired(1522, "用户认证(登录)过期", noAccount),
    userCredentialsError(1523, "用户凭证不匹配", noAccount),
    userCredentialsExpired(1524, "用户凭证已过期", noAccount),
    userCredentialsUnsupported(1525, "用户凭证不被支持", noAccount),

    disabledUser(1526, "用户账号不可用", noAccount),
    idCardError(1527, "身份证错误", noAccount),
    idCardExpired(1528, "身份证过期", noAccount),
    duplicateUser(1529, "重复用户(已被注册)", noAccount),

    /*----------------------------- 账户类状态类[1500-2999](如:账户锁定) end ----------------------------------------------*/

    /*----------------------------- 存管业务异常[3000-3099] start ----------------------------------------------*/


    /*----------------------------- 存管业务异常[3000-3099] end ----------------------------------------------*/

    /*----------------------------- 第三方业务异常[3100-3200] start ----------------------------------------------*/
    projectExists(3100, "标的已存在",null),
    projectNotExists(3101, "标的不存在",projectExists),
    projectTypeNotExists(3102, "标的类型不存在",projectExists),
    typeNotMatchProject(3103, "标的类型与渠道不匹配",projectExists),

    generalError(3104, "通用错误，错误信息可自定义",projectExists),
    projectRepayStatusError(3105, "标的当前状态非还款中",projectExists),
    prepaymentPrincipalAmountError(3106, "提前还款本金金额不匹配",projectExists),
    prepaymentInterestAmountError(3107, "提前还款利息金额不匹配",projectExists),

    prepaymentFeeAmountError(3108, "提前还款服务费金额不匹配",projectExists),
    overdueRepaymentCompensatoryError(3109, "逾期还款当期未找到已代偿支付记录",projectExists),
    prepaymentTotalAmountError(3110, "还款总金额不匹配",projectExists),

    limitMatchAmountLack(3111, "剩余可匹配金额不足", projectExists),
    proxyRepayExist(3112,"代扣记录存在",projectExists),
    userAuthOverdue(3113, "用户授权已过期", projectExists),


    /*----------------------------- 第三方业务异常[3100-3200] end ----------------------------------------------*/


    /*----------------------------- 分布式异常[6000-7000] start ----------------------------------------------*/
    distributeLockLose(6000,"分布式锁丢失",null),
    /*----------------------------- 分布式异常[6000-7000] end ----------------------------------------------*/


    /*----------------------------- 下载异常[7001-7300] start ----------------------------------------------*/
    downloadMaxRestrict(7001,"下载任务最大限制",null),

    notFoundDownloadFile(7002,"没有找到下载文件",downloadMaxRestrict),
    downloadTaskDuplicate(7003,"下载任务重复",downloadMaxRestrict),
    notDownloadPhase(7004,"文件还不能下载",downloadMaxRestrict),
    downloadError(7005,"下载出错",downloadMaxRestrict),

    /*----------------------------- 下载异常[7301-7600] end ----------------------------------------------*/

    /*----------------------------- 上传异常[7001-7300] start ----------------------------------------------*/
    serverUploadError(7301,"服务端上传出错",null),

    /*----------------------------- 上传异常[7301-7600] end ----------------------------------------------*/


    /*-----------------------------  资源异常[8001-9000] start ----------------------------------------------*/

    resourceNotFund(8001,"资源未找到",null),
    resourceCreateError(8002,"资源创建异常",resourceNotFund),

    /*-----------------------------  资源异常[8001-9000] end ----------------------------------------------*/

    /*############################# 模板处理异常[9001-9020] start  ########################################*/
    // 如: Thymeleaf
    templateProcessError(9001,"模板处理错误",null),

    /*############################# 模板处理异常[9001-9020] end  ########################################*/
    // 如:html 转换为 pdf
    docConvertError(9021,"文档转换出错",null),
    notFoundFont(9022,"未找到相应字体",docConvertError),

    /*############################# 服务端的数据问题[9100-9199] start  ########################################*/
    serverDataException(9100,"服务端数据异常",null),
    dataNotCreate(9101,"服务端数据还未生成",serverDataException),
    /*############################# 服务端的数据问题[9100-9199] end  ########################################*/

    /*############################# 雪花算法分配器问题[9200-9299] start  ########################################*/
    snowflake(9200, "雪花算法 worker 错误", null),
    noAssignedWorker(9201, "本结点没有分配到 worker", snowflake),
    noWorker(9202, "服务端没有可分配的 worker", snowflake),
    workerForbid(9203, "客户端上报的worker已被禁用", snowflake),

    workerClientError(9220, "雪花算法客户端出错", snowflake),
    workerNodeAssigned(9221, "客户端 workerNode 已分配过", workerClientError),
    workerNotMatch(9222, "客户端上报的worker与服务端存储的客户端的worker不匹配", workerClientError),
    workerNodeAbsent(9223, "客户端上报的workerNode不存在", workerClientError),

    workerNotAssign(9224, "客户端上报的worker未分配", workerClientError),
    workerNodeExpired(9225, "workerNode 心跳过期", workerClientError),


    workerServerError(9250, "雪花算法服务端出错", snowflake),
    assignedStoreError(9251, "worker 的已分配存储错误", workerServerError),
    workerCountError(9252, "worker 数量大于 1024", workerServerError),


    /*############################# 雪花算法分配器问题[9200-9299] end  ########################################*/
    ;

    private static final Set<Integer> PRESERVE = initPreserve();

    private static final Map<Integer, ResultCode> CODE_MAP = CodeEnum.createCodeMap( ResultCode.class );


    private static Set<Integer> initPreserve() {
        Set<Integer> set = new HashSet<>();
        set.add(100);
        set.add(101);

        set.add(201);
        set.add(202);
        set.add(203);
        set.add(204);

        set.add(205);
        set.add(206);

        set.add(301);
        set.add(302);
        set.add(303);
        set.add(304);

        set.add(305);
        set.add(306);
        set.add(307);


        set.add(402);
        set.add(403);
        set.add(407);
        set.add(408);

        set.add(409);
        set.add(410);
        set.add(411);
        set.add(412);

        set.add(413);
        set.add(414);
        set.add(416);

        set.add(417);


        set.add(501);
        set.add(502);
        set.add(505);

        return Collections.unmodifiableSet(set);
    }

    private final int code;

    private final String display;

    /**
     * 用于表示 一个大类的错误.
     */
    private final ResultCode family;

    ResultCode(int code, String display, ResultCode family) {
        this.code = code;
        this.display = display;
        this.family = family == null ? this : family;
    }


    @Override
    public int code() {
        return code;
    }

    @Override
    public String display() {
        return display;
    }

    @Override
    public ResultCode family(){
        return family;
    }


    public static ResultCode resolve(int code) {
        return CODE_MAP.get(code);
    }


}
