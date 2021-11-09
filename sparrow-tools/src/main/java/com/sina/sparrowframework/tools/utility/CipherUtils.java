package com.sina.sparrowframework.tools.utility;


import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.PublicKey;
import java.util.UUID;

/**
 * 加解密工具-禁止自己封装加密工具
 *
 * @author
 * @see KeyUtils
 */
public abstract class CipherUtils {

    private static final Logger LOG = LoggerFactory.getLogger(CipherUtils.class);

    /**
     * 填充模型枚举类型
     */
    public enum Algorithm {
        AES("AES"),
        RSA("RSA"),
        AES_CBC_NO_PADDING("AES/CBC/NoPadding"),
        AES_CBC_PKCS5PADDING("AES/CBC/PKCS5Padding"),
        AES_ECB_NOPADDING("AES/ECB/NoPadding"),
        AES_ECB_PKCS5PADDING("AES/ECB/PKCS5Padding"),
        RSA_ECB_PKCS1PADDING("RSA/ECB/PKCS1Padding"),
        RSA_ECB_OAEPWITHSHA1_ANDMGF1PADDING("RSA/ECB/OAEPWithSHA-1AndMGF1Padding"),
        RSA_ECB_OAEPWITHSHA256_ANDMGF1PADDING("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        private final String algorithm;

        Algorithm(String algorithm) {
            this.algorithm = algorithm;
        }
    }
    /*---------------填充模式加解密方法--------------*/

    /**
     * 填充模型:AES_ECB_PKCS5PADDING
     * @param cipherText 密文数据
     * @param password 秘钥
     * @return 解密数据
     * @throws Exception
     */
    public static String decryptByAesEcbPkcs5padding(
            String cipherText, String password) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(
                password.getBytes(), Algorithm.AES_ECB_PKCS5PADDING.algorithm);
        return decrypt(keySpec,cipherText);
    }
    /**
     * 填充模型:AES_ECB_PKCS5PADDING
     * @param data 明文数据
     * @param password 秘钥
     * @return 加密后数据Base64
     * @throws Exception
     */
    public static String encryptByAesEcbPkcs5padding(
            String data
            , String password) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(
                password.getBytes(), Algorithm.AES_ECB_PKCS5PADDING.algorithm);
        return encryptText(keySpec,data);
    }


    /*---------------以下是加密重载方法--------------*/

    /**
     * 解密 密文
     * <p>
     * 默认使用 {@link StandardCharsets#UTF_8}
     * </p>
     *
     * @param key        解密的密钥,not null
     * @param cipherText 密文(base64编码), not null
     * @return 明文 or null ,如果失败则返回 null
     * @throws Exception - 解密失败
     */
    public static String decrypt(Key key, String cipherText) throws Exception {
        byte[] decryptByte = decrypt(key, Base64.decodeBase64(cipherText));
        return new String(decryptByte, StandardCharsets.UTF_8);
    }

    public static byte[] decrypt(Key key, byte[] cipherContent) throws Exception {
        Cipher cipher = Cipher.getInstance(key.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, key);

        return cipher.doFinal(cipherContent);
    }


    public static void decrypt(Key key
            , Algorithm algorithm
            , InputStream in
            , File file)
            throws Exception {
        decrypt(key, algorithm, in, new FileOutputStream(file));
    }

    public static void decrypt(Key key
            , Algorithm algorithm
            , InputStream in
            , OutputStream outputStream)
            throws Exception {

        Cipher cipher = Cipher.getInstance(algorithm.algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key);

        copyStreamWithCipher(in, outputStream, cipher);
    }

    public static File decrypt(Key key
            , Algorithm algorithm
            , InputStream in) throws Exception {
        File file = new File(
                System.getProperty("java.io.tmpdir")
                , UUID.randomUUID().toString());
        if (!file.exists() && file.createNewFile()) {
            LOG.info("创建临时文件,{}", file.getAbsolutePath());
        }
        decrypt(key, algorithm, in, new FileOutputStream(file));
        return file;
    }


    public static byte[] decrypt(Key key
            , Algorithm algorithm
            , byte[] cipherContent) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithm.algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key);

        return cipher.doFinal(cipherContent);
    }

    public static byte[] decrypt(String keyBase64
            , Algorithm algorithm
            , byte[] cipherContent) throws Exception {
        Key key = KeyUtils.readAesKey(keyBase64);
        return decrypt(key, algorithm, cipherContent);
    }

    public static String decrypt(String keyBase64
            , Algorithm algorithm
            , String cipherText) throws Exception {
        byte[] decryptByte = decrypt(keyBase64
                , algorithm, Base64.decodeBase64(cipherText));
        return new String(decryptByte, StandardCharsets.UTF_8);
    }

    public static byte[] decryptWithRsaPublic(String keyBase64
            , Algorithm algorithm
            , byte[] cipherContent) throws Exception {
        Key key = KeyUtils.readRsaPublicKey(keyBase64);
        return decrypt(key, algorithm, cipherContent);
    }

    /**
     * RSA 公钥解密
     * @param keyBase64 秘钥
     * @param algorithm 填充模式
     * @param cipherText 密文
     * @return 明文数据
     * @throws Exception
     */
    public static String decryptWithRsaPublic(String keyBase64
            , Algorithm algorithm
            , String cipherText) throws Exception {

        byte[] decryptByte = decryptWithRsaPublic(keyBase64
                , algorithm,
                Base64.decodeBase64(cipherText));
        return new String(decryptByte, StandardCharsets.UTF_8);
    }

    /**
     * 处理解密data 128 bytes
     * @param keyBase64
     * @param algorithm
     * @param cipherText
     * @return
     * @throws Exception
     */
    public static String decryptWithRsaPublic128(String keyBase64
            , Algorithm algorithm
            , String cipherText) throws Exception {
        PublicKey key = KeyUtils.readRsaPublicKey(keyBase64);
        Cipher cipher = Cipher.getInstance(algorithm.algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(multiSlot(new ByteArrayInputStream(
                Base64.decodeBase64(cipherText)),cipher,128),StandardCharsets.UTF_8);
    }
    private static byte[] multiSlot(InputStream input, Cipher cipher, int maxLength)
            throws IOException, BadPaddingException, IllegalBlockSizeException {

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             InputStream in = new BufferedInputStream(input)) {
            byte[] buffer = new byte[maxLength];
            int read;
            while ((read = in.read(buffer, 0, maxLength)) != -1) {
                out.write(cipher.doFinal(buffer, 0, read));
            }
            return out.toByteArray();
        }
    }


    public static byte[] decryptWithRsaPrivate(String keyBase64
            , Algorithm algorithm
            , byte[] cipherContent) throws Exception {

        Key key = KeyUtils.readRsaPrivateKey(keyBase64);
        return decrypt(key, algorithm, cipherContent);
    }
    /**
     * RSA 私钥解密
     * @param keyBase64 秘钥
     * @param algorithm 填充模式
     * @param cipherText 密文
     * @return 明文数据
     * @throws Exception
     */
    public static String decryptWithRsaPrivate(String keyBase64
            , Algorithm algorithm
            , String cipherText) throws Exception {
        byte[] decryptByte = decryptWithRsaPrivate(
                keyBase64, algorithm, Base64.decodeBase64(cipherText));
        return new String(decryptByte, StandardCharsets.UTF_8);
    }

    /*---------------以下是加密重载方法--------------*/

    /**
     * 加密 明文
     * <p>
     * 默认使用 {@link StandardCharsets#UTF_8}
     * </p>
     *
     * @param key       加密的密钥,not null
     * @param plaintext 明文, not null
     * @return 密文 (base64 编码) or null ,如果失败则返回 null
     * @throws RuntimeException - 解密失败
     */
    public static String encryptText(Key key
            , String plaintext) throws Exception {
        byte[] encryptByte = encrypt(key, plaintext.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeBase64String(encryptByte);

    }

    public static byte[] encrypt(Key key, byte[] plainTextContent) throws Exception {
        Cipher cipher = Cipher.getInstance(key.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, key);

        return cipher.doFinal(plainTextContent);
    }

    public static void encrypt(Key key
            , Algorithm algorithm
            , InputStream in, OutputStream outputStream)
            throws Exception {
        Cipher cipher = Cipher.getInstance(algorithm.algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        copyStreamWithCipher(in, outputStream, cipher);
    }

    public static void encrypt(Key key
            , Algorithm algorithm
            , InputStream in, File file)
            throws Exception {
        encrypt(key, algorithm, in, new FileOutputStream(file));
    }

    public static File encrypt(Key key
            , Algorithm algorithm
            , InputStream in) throws Exception {
        File file = new File(System.getProperty("java.io.tmpdir")
                , UUID.randomUUID().toString());
        if (!file.exists() && file.createNewFile()) {
            LOG.info("创建临时文件,{}", file.getAbsolutePath());
        }
        encrypt(key, algorithm, in, new FileOutputStream(file));
        return file;
    }

    public static byte[] encrypt(
            Key key
            , Algorithm algorithm
            , byte[] plainTextContent) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithm.algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        return cipher.doFinal(plainTextContent);
    }

    public static byte[] encrypt(String keyBase64
            , Algorithm algorithm
            , byte[] plainTextContent) throws Exception {

        Key key = KeyUtils.readAesKey(keyBase64);

        return encrypt(key, algorithm, plainTextContent);
    }

    public static String encrypt(String keyBase64
            , Algorithm algorithm
            , String plaintext) throws Exception {

        byte[] encryptByte = encrypt(keyBase64
                , algorithm
                , plaintext.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeBase64String(encryptByte);
    }


    public static byte[] encryptWithRsaPublic(
            String keyBase64
            , Algorithm algorithm
            , byte[] plainTextContent) throws Exception {

        Key key = KeyUtils.readRsaPublicKey(keyBase64);

        return encrypt(key, algorithm, plainTextContent);
    }

    /**
     * RSA 公钥加密方法
     * @param keyBase64 公钥
     * @param algorithm 填充模型
     * @param plaintext 明文数据
     * @return 加密数据
     * @throws Exception
     */
    public static String encryptWithRsaPublic(
            String keyBase64
            , Algorithm algorithm
            , String plaintext) throws Exception {

        byte[] encryptByte = encryptWithRsaPublic(
                keyBase64
                , algorithm
                , plaintext.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeBase64String(encryptByte);
    }

    public static byte[] encryptWithRsaPrivate(
            String keyBase64
            , Algorithm algorithm
            , byte[] plainTextContent) throws Exception {
        Key key = KeyUtils.readRsaPrivateKey(keyBase64);

        return encrypt(key, algorithm, plainTextContent);
    }

    /**
     * RSA私钥加密
     * @param keyBase64 秘钥
     * @param algorithm 填充模式
     * @param plaintext 明文数据
     * @return 加密数据
     * @throws Exception
     */
    public static String encryptWithRsaPrivate(
            String keyBase64
            , Algorithm algorithm
            , String plaintext) throws Exception {

        byte[] encryptByte = encryptWithRsaPrivate(
                keyBase64, algorithm
                , plaintext.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeBase64String(encryptByte);
    }

    private static void copyStreamWithCipher(
            InputStream inputStream
            , OutputStream outputStream, Cipher cipher)
            throws Exception {
        try (InputStream in = inputStream; OutputStream out = outputStream) {
            byte[] buffer = new byte[2048], outBuffer;
            int len;
            for (; (len = in.read(buffer)) > 0; ) {
                outBuffer = cipher.update(buffer, 0, len);
                if (outBuffer != null) {
                    out.write(outBuffer);
                }
            }
            outBuffer = cipher.doFinal();
            if (outBuffer != null) {
                out.write(outBuffer);
            }
        }
    }
}
