package com.sina.sparrowframework.password.licai;

import com.sina.sparrowframework.tools.utility.StrPool;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wxn
 * @date 2021/6/11 2:09 下午
 */
public final class AesConstant {
    /**
     * 渠道CODE
     */
    public static final String APP_ID_CHANNEL = "global.%s.channelCode";
    /**
     * 私钥Key
     */
    public static final String APP_PRIVATE_KEY = "global.%s.private.key";
    /**
     * 公钥Key
     */
    public static final String APP_PUBLIC_KEY = "global.%s.public.key";
    /**
     * 白名单
     */
    public static final String APP_WHITE_IPS = "global.%s.ips";
    /**
     * 白名单开端
     */
    public static final String APP_WHITE_IPS_SWITCH = "global.%s.ips.switch";

    public static final String APP_ID = "appId";
    public static final String APP_ID_NAME = "appID";
    public static final String APP_PASSWORD = "passwd";
    public static final String APP_DATA = "data";

    private static String unknown = "unknown";

    public static String  getRequestIp(HttpServletRequest request) {
        if (request == null) {
            throw new RuntimeException("HTTP servlet request is null!");
        }
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        } else if (ip.indexOf(StrPool.COMMA) > 0) {
            String[] ipArr = ip.split(StrPool.COMMA);
            ip = ipArr[0];
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

}
