package com.sina.sparrowframework.password.licai;

import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Random;


public class AESUtil {
    /**
     * aes加密
     *
     * @param inputStr
     * @param password
     * @return
     * @throws Exception
     */
    public static String encryptByAES(String inputStr, String password) throws Exception {
        byte[] byteData = inputStr.getBytes();
        byte[] bytePassword = password.getBytes();
        return RSASign.encryptBASE64(encryptByAES(byteData, bytePassword));
    }

    public static byte[] encryptByAES(byte[] data, byte[] pwd) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec keySpec = new SecretKeySpec(pwd, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] ret = cipher.doFinal(data);
        return ret;
    }

    public static String decryptByAES(String inputStr, String password) throws Exception {
        byte[] byteData = RSASign.decryptBASE64(inputStr);
        byte[] bytePassword = password.getBytes();
        return new String(decryptByAES(byteData, bytePassword));
    }

    public static byte[] decryptByAES(byte[] data, byte[] pwd) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec keySpec = new SecretKeySpec(pwd, "AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] ret = cipher.doFinal(data);
        return ret;
    }

    /**
     * 生成16位AES随机串
     *
     * @return
     */
    public static String getRandomKey() {
        Random random = new Random();
        long longValue = random.nextLong();
        return String.format("%016x", longValue);
    }

    /**
     * 产生6位随机数
     *
     * @param length
     * @return
     */
    public static String getRandomString(int length) { // length表示生成字符串的长度
        return getRandomStr("012345678917219219201177112919013737312109128173361631281190281", length);
    }

    /**
     * length表示生成字符串的长度
     *
     * @param length
     * @return
     */
    public static String getRandomStr(String base, int length) {
        if (StringUtils.isEmpty(base)) {
            base = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        }
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

}
