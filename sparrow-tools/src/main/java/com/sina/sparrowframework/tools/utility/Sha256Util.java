package com.sina.sparrowframework.tools.utility;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sha256Util {

    /**
     * 利用java原生的类实现SHA256加密
     *
     * @param str 加密后的报文
     * @return
     */
    public static String getSHA256(String str) {
        MessageDigest messageDigest = null;
        String encodestr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes("UTF-8"));
            encodestr = byte2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodestr;
    }

    /**
     * 将byte转为16进制
     *
     * @param bytes
     * @return
     */
    private static String byte2Hex(byte[] bytes) {
        String temp = null;
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length() == 1) {
                // 1得到一位的进行补0操作
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }

    /**
     * macSHA256加密签名工具类
     * @param payload
     * @param secret
     * @return
     */
    public static String encryptByMacSha256(String payload, String secret) {
        String hash = "";
        try {
            Mac hmacSHA256 = Mac.getInstance("hmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "hmacSHA256");
            hmacSHA256.init(secret_key);
            byte[] bytes = hmacSHA256.doFinal(payload.getBytes());
            hash =  Base64.encodeBase64URLSafeString(bytes);
        } catch (Exception e) {
           throw new RuntimeException(e);
        }
        return hash;
    }
}
