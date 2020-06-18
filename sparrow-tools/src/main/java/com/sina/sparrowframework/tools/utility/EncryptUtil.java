package com.sina.sparrowframework.tools.utility;


import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class EncryptUtil {

    private static Key key = KeyUtils.readAesKey("cepPbvdple1V78n7asNgFQ==");
    private static int NUM = 4;
    private static int PHONE_NUM = 11;
    private static int NAME_NUM = 2;
    private static String ONLY_DESENSITIZATION_VALUE = "*";
    private static String DESENSITIZATION_VALUE = "****";

    /**
     * 用户敏感信息用解密
     *
     * @param decryptext
     * @return
     */
    public static String decryptText(String decryptext) {
        if (decryptext == null) {
            return null;
        }
        try {
            return CipherUtils.decrypt(key, decryptext);
        } catch (Exception e) {
            return decryptext;
        }
    }


    /**
     * 用户敏感信息用加密
     *
     * @param plaintext
     * @return
     */
    public static String encryptText(String plaintext) {
        if (plaintext == null) {
            return null;
        }
        try {
            return CipherUtils.encryptText(key, plaintext);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 身份证信息脱敏后四位
     *
     * @param idCard
     * @return
     */
    public static String desensitizationIdCard(String idCard) {
        if (idCard == null) {
            return null;
        }
        if (idCard.length() > NUM) {
            idCard = idCard.substring(0, idCard.length() - 4) + DESENSITIZATION_VALUE;
        } else {
            idCard = DESENSITIZATION_VALUE;
        }
        return idCard;
    }

    /**
     * 手机号信息脱敏后四位
     *
     * @param phone
     * @return
     */
    public static String desensitizationPhone(String phone) {
        if (phone == null) {
            return null;
        }
        if (phone.length() == PHONE_NUM) {
            phone = phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
        } else {
            phone = DESENSITIZATION_VALUE;
        }
        return phone;
    }

    /**
     * 姓名脱敏
     * 俩个字得脱敏第二个字
     * 三个字以上得脱敏中间字只保留第一个字与最后一个字
     *
     * @param name 要脱敏得名字
     * @return
     */
    public static String desensitizationName(String name) {
        if (StringToolkit.isEmpty(name)) {
            return name;
        }
        char[] chars = name.toCharArray();
        if (name.length() > NAME_NUM) {
            name = chars[0] + ONLY_DESENSITIZATION_VALUE + ONLY_DESENSITIZATION_VALUE;
        } else {
            name = chars[0] + ONLY_DESENSITIZATION_VALUE;
        }
        return name;
    }

    /**
     * 对用户的名字与身份证信息进行加密
     *
     * @param plaintext 需要加密的参数
     * @return 加密后得信息
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static String encryptShe256Test(String plaintext) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(plaintext.getBytes(Charset.forName("UTF-8")));

        byte byteData[] = md.digest();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

}
