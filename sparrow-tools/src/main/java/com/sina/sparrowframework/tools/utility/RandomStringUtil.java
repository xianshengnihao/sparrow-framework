package com.sina.sparrowframework.tools.utility;

import java.util.Random;


public class RandomStringUtil {
    private static String BASE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";

    public static final String NUMBER_CHARS = "0123456789";

    private static String SPECIA_CHARS = "!@#$%^&*";

    /**
     * 生成指定长度字符串，默认不包含特殊字符
     *
     * @param length 生成字符串长度
     * @return
     */
    public static String getRandomString(int length) {
        return getRandomString(length, false);
    }

    /**
     * 生成随机字符串
     *
     * @param length     生成字符串长度
     * @param hasSpecial 是否包含特殊字符
     * @return
     */
    public static String getRandomString(int length, boolean hasSpecial) {
        String chars = hasSpecial ? BASE_CHARS.concat(SPECIA_CHARS) : BASE_CHARS;
        return getRandomString(length, chars);
    }

    /**
     * 在指定的字符内生成指定长度字符串
     *
     * @param length
     * @param string
     * @return
     */
    public static String getRandomString(int length, String string) {
        int len = string.length();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int index = (int) Math.round(Math.random() * (len - 1));
            sb.append(string.charAt(index));
        }
        return sb.toString();
    }

    /**
     * 生成length位数字字符串
     *
     * @param length
     * @return
     */
    public static String getId(int length) {
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(NUMBER_CHARS.charAt(random.nextInt(NUMBER_CHARS.length())));
        }
        return sb.toString();
    }
}


