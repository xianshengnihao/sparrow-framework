package com.sina.sparrowframework.tools.utility;


import org.apache.commons.codec.binary.Base64;

import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.*;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.regex.Pattern;

public abstract class KeyUtils {


    protected static final String KEY_BEGIN = "-----BEGIN %s %s KEY-----";

    protected static final String KEY_END = "-----END %s %s KEY-----";

    private static final Pattern BOUNDARY_PATTERN = Pattern.compile("(?:^\\s*-+[^-]+-+\\s*(?=[^-])|(?<=[^-])\\s*-+[^-]+-+\\s*$)");




    protected KeyUtils() {

    }


    /**
     * 读取 base64 表示的 AES key.
     * <p>
     * 上下边界(可选)分别为
     * <ul>
     * <li>-----BEGIN AES KEY-----</li>
     * <li>-----END AES KEY-----</li>
     * </ul>
     * </p>
     *
     * @param base64Text AES key 的 base 64 表示,not null
     * @throws RuntimeException - 读取失败
     */
    public static Key readAesKey(String base64Text) throws RuntimeException {
        try {
            return readKey(KeyType.AES,base64Text);
        } catch (Exception e) {
           throw new RuntimeException(e.getMessage(),e);
        }

    }



    /**
     * 从 base64 中读取 RSA 私钥
     * <p>
     * 上下边界(可选)分别为
     * <ul>
     * <li>-----BEGIN RSA PRIVATE KEY-----</li>
     * <li>-----END RSA PRIVATE KEY-----</li>
     * </ul>
     * </p>
     *
     * @param base64Text RSA 私钥 的 base64 表示
     * @return RSA 私钥
     * @throws Exception - 读取失败
     */
    public static PrivateKey readRsaPrivateKey(String base64Text) throws Exception {
        return readPrivateKey(KeyPairType.RSA,base64Text);

    }

    /**
     * 从 base64 中读取 RSA 公钥
     * <p>
     * 上下边界(可选)分别为
     * <ul>
     * <li>-----BEGIN RSA PUBLIC KEY-----</li>
     * <li>-----END RSA PUBLIC KEY-----</li>
     * </ul>
     * </p>
     *
     * @param base64Text RSA 公钥的 base64 表示
     * @return RSA 公钥
     * @throws Exception - 读取失败
     */
    public static PublicKey readRsaPublicKey(String base64Text) throws Exception {
        return readPublicKey(KeyPairType.RSA,base64Text);
    }


    /**
     * @return AES key
     * @throws Exception 创建失败, key size 错误
     */
    public static Key createAesKey(int keySize) throws Exception {
        return createKey(KeyType.AES,keySize);
    }

    /**
     * 创建 RSA 密钥对
     *
     * @return RSA 密钥对
     */
    public static KeyPair createRsaKeyPair(int keySize) throws Exception {
        return createKeyPari(KeyPairType.RSA,keySize);
    }

    /**
     * 将 key 写入到指定的文件
     *
     * @throws IOException - 文件写入异常,如:文件不存在,或没有写入权限
     */
    public static void writeTo(File file, Key key) throws IOException {

        try (FileWriter w = new FileWriter(file); BufferedWriter writer = new BufferedWriter(w)) {

            doWriteToFile(writer, key);

        }
    }


    public static String writeToString(Key key) {
        return Base64.encodeBase64String( key.getEncoded() );
    }


    public static Key readKey(KeyType type, String base64Text) throws Exception {
        KeySpec spec = getKeySpec(type, getEncoded(base64Text));
        Key key ;
        if (type == KeyType.AES) {
            key = (Key) (spec);
        }else {
            throw new Exception("不支持 key type");
        }
        return key;
    }

    public static PrivateKey readPrivateKey(KeyPairType type, String base64Text) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(type.getDisplay());
        return keyFactory.generatePrivate(
                getKeySpec(type, true, getEncoded(base64Text))
        );
    }

    public static PublicKey readPublicKey(KeyPairType type, String base64Text) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(type.getDisplay());
        return keyFactory.generatePublic(
                getKeySpec(type, false, getEncoded(base64Text))
        );
    }

    public static Key createKey(KeyType type, int keySize) throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(type.getDisplay());
        keyGenerator.init(keySize);
        return keyGenerator.generateKey();
    }

    public static KeyPair createKeyPari(KeyPairType type, int keySize) throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance(type.getDisplay());
        generator.initialize(keySize);
        return generator.generateKeyPair();
    }






    /*##################### protected ########################*/

    protected static KeySpec getKeySpec(KeyType type, byte[] encoded) {
        KeySpec spec;

        switch (type) {
            case AES:
                spec = new SecretKeySpec(encoded, type.getDisplay());
                break;
            default:
                throw new IllegalArgumentException(String.format("KeyAlgorithm[%s] unknown", type));
        }
        return spec;
    }

    protected static KeySpec getKeySpec(KeyPairType type, boolean privateKey, byte[] encoded) {
        KeySpec spec;

        switch (type) {
            case RSA:
                spec = privateKey ? new PKCS8EncodedKeySpec(encoded) : new X509EncodedKeySpec(encoded);
                break;
            default:
                throw new IllegalArgumentException(String.format("KeyAlgorithm[%s] unknown", type));
        }
        return spec;
    }


    protected static byte[] getEncoded( String base64Text) {
        return Base64.decodeBase64(
                BOUNDARY_PATTERN.matcher(base64Text).replaceAll("")
        );
    }

    /*##################### private ########################*/

    private static void doWriteToFile(BufferedWriter writer, Key key)
            throws IOException {
        final String base64 = Base64.encodeBase64String(key.getEncoded()).replaceAll("\\r?\\n?", "");
        String typeDesc ;
        if(key instanceof PrivateKey){
                typeDesc = "PRIVATE";
        }else if (key instanceof PublicKey){
            typeDesc = "PUBLIC";
        }else {
            typeDesc = "";
        }
        //写入上边界
        writer.write(String.format(KEY_BEGIN, key.getAlgorithm(),typeDesc));
        writer.newLine();

        final int bit = 6, size = 1 << bit;
        int start, end;
        int count = base64.length() / size;
        count = base64.length() % size == 0 ? count : count + 1;

        for (int i = 0; i < count; i++) {
            //计算 start and end
            start = i << bit;
            end = start + size;
            end = Math.min(end, base64.length());
            //写入一行
            writer.write(base64.substring(start, end));
            writer.newLine();
        }
        //写入下边界
        writer.write(String.format(KEY_END, key.getAlgorithm(),typeDesc));
    }





}
