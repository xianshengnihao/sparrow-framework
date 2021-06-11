package com.sina.sparrowframework.password.licai;


import com.sina.sparrowframework.tools.utility.SignatureType;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;


public class RSASign {

    /**
     * base64解密
     *
     * @param key
     * @return
     * @throws IOException
     * @throws Exception
     */
    public static byte[] decryptBASE64(String key) throws IOException {
        return Base64.getDecoder().
                decode(key.replace("\r\n", "")
                        .replace("\n", "").getBytes(StandardCharsets.UTF_8));

    }

    /**
     * base64加密
     *
     * @param key
     * @return
     * @throws Exception
     */
    public static String encryptBASE64(byte key[]) {
        return Base64.getEncoder().encodeToString(key);
    }

    /**
     * 获取 PublicKey
     *
     * @param base64String
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws IOException
     */
    public static PublicKey getPublicKeyFromString(String base64String)
            throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        byte[] bt = decryptBASE64(base64String);//解密base64
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(bt);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
        return publicKey;
    }

    /**
     * 获取privateKey
     *
     * @param base64String
     * @return
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public static PrivateKey getPrivateKeyFromString(String base64String)
            throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
        byte[] bt = decryptBASE64(base64String);
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(bt);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
        return privateKey;
    }


    /**
     * @param
     * @param
     * @return
     * @throws Exception
     */
    public static String encryptByRSA(String password, String publicKey2) throws Exception {
        PublicKey key = getPublicKeyFromString(publicKey2);
        return encryptBASE64(encryptByRSA(password.getBytes(), key));
    }

    /**
     * @param input
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] encryptByRSA(byte[] input, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] output = cipher.doFinal(input);
        return output;
    }

    public static String encryptByRSAPri(String inputStr, String privateKey) throws Exception {
        PrivateKey key = getPrivateKeyFromString(privateKey);
        return encryptBASE64(encryptByRSA(inputStr.getBytes(StandardCharsets.UTF_8), key));
    }

    public static String signByPrivateKey(String inputStr, String privateKey) throws Exception {
        return encryptBASE64(signByPrivateKey(SignatureType.SHA256withRSA, inputStr.getBytes(), getPrivateKeyFromString(privateKey)));
    }

    public static String signByPrivateKey(SignatureType signatureType, String inputStr, String privateKey) throws Exception {
        return encryptBASE64(signByPrivateKey(signatureType, inputStr.getBytes(StandardCharsets.UTF_8), getPrivateKeyFromString(privateKey)));
    }

    public static byte[] signByPrivateKey(SignatureType signatureType, byte[] data, PrivateKey privateKey) throws Exception {
        Signature sig = Signature.getInstance(signatureType.name());
        sig.initSign(privateKey);
        sig.update(data);
        byte[] ret = sig.sign();
        return ret;
    }

    /**
     * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     *
     * @param params 需要排序并参与字符拼接的参数组
     * @return 拼接后字符串
     */
    public static String createLinkString(Map<String, Object> params) {
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        StringBuilder linkStr = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = (String) params.get(key);
            if (value == null) {
                continue;
            }
            if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
                linkStr.append(key).append("=").append(value);
            } else {
                linkStr.append(key).append("=").append(value).append("&");
            }
        }
        return linkStr.toString();
    }


    public static boolean verifyByPublicKey(byte[] data, PublicKey publicKey, byte[] signature) throws Exception {
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initVerify(publicKey);
        sig.update(data);
        boolean ret = sig.verify(signature);
        return ret;
    }


    public static String decryptByRSA(String inputStr, String privatekey) throws Exception {
        PrivateKey key = getPrivateKeyFromString(privatekey);
        return new String(decryptByRSA(decryptBASE64(inputStr), key), StandardCharsets.UTF_8);
    }

    public static String decryptByRSAPub(String inputStr, String publicKey) throws Exception {
        PublicKey key = getPublicKeyFromString(publicKey);
        return new String(decryptByRSA(decryptBASE64(inputStr), key), StandardCharsets.UTF_8);
    }

    public static String decryptByRSAPublic(String inputStr, String publicKey) throws Exception {
        PublicKey key = getPublicKeyFromString(publicKey);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(multiSlot(new ByteArrayInputStream(
                decryptBASE64(inputStr)),cipher,128),StandardCharsets.UTF_8);
    }

        /**
         * @param input
         * @param key
         * @return
         * @throws Exception
         */
    public static byte[] decryptByRSA(byte[] input, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] output = cipher.doFinal(input);
        return output;
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

    /**
     * @param
     * @return 签名后的sign值
     * @throws Exception
     * @author yzzhi
     */
    public static String getSign(Map<String, Object> params, String privatekey) throws Exception {
        //privatekey
        return signByPrivateKey(createLinkString(params), privatekey);
    }

    public static boolean verifyByPublicKey(SignatureType signatureType, String content, String sign, String publicKey) throws Exception {
        Signature signature = Signature.getInstance(signatureType.name());
        signature.initVerify(getPublicKeyFromString(publicKey));
        signature.update(content.getBytes(StandardCharsets.UTF_8));
        return signature.verify(org.apache.commons.codec.binary.Base64.decodeBase64(sign));

    }

}
