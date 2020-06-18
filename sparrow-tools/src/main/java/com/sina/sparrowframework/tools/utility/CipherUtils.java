package com.sina.sparrowframework.tools.utility;


import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import javax.crypto.Cipher;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.UUID;

/**
 * 加解密工具
 * created  on 23/01/2018.
 *
 * @see KeyUtils
 */
public abstract class CipherUtils {

    private static final Logger LOG = LoggerFactory.getLogger( CipherUtils.class );


    public enum Algorithm {
        AES( "AES" ),

        RSA( "RSA" ),

        AES_CBC_NO_PADDING( "AES/CBC/NoPadding" ),

        AES_CBC_PKCS5PADDING( "AES/CBC/PKCS5Padding" ),

        AES_ECB_NOPADDING( "AES/ECB/NoPadding" ),

        AES_ECB_PKCS5PADDING( "AES/ECB/PKCS5Padding" ),


        RSA_ECB_PKCS1PADDING( "RSA/ECB/PKCS1Padding" ),

        RSA_ECB_OAEPWITHSHA1_ANDMGF1PADDING( "RSA/ECB/OAEPWithSHA-1AndMGF1Padding" ),

        RSA_ECB_OAEPWITHSHA256_ANDMGF1PADDING( "RSA/ECB/OAEPWithSHA-256AndMGF1Padding" );

        private final String algorithm;

        Algorithm(String algorithm) {
            this.algorithm = algorithm;
        }


    }


    /**
     * 解密 密文
     * <p>
     * 默认使用 {@link StandardCharsets#UTF_8}
     * </p>
     *
     * @param key        解密的密钥,not null
     * @param ciphertext 密文(base64编码), not null
     * @return 明文 or null ,如果失败则返回 null
     * @throws Exception - 解密失败
     */
    public static String decrypt(Key key, String ciphertext) throws Exception {
        byte[] decryptByte = decrypt( key, Base64.decodeBase64( ciphertext ) );
        return new String( decryptByte, StandardCharsets.UTF_8 );
    }

    public static byte[] decrypt(Key key, byte[] cipherContent) throws Exception {
        Cipher cipher = Cipher.getInstance( key.getAlgorithm() );
        cipher.init( Cipher.DECRYPT_MODE, key );

        return cipher.doFinal( cipherContent );
    }


    public static void decrypt(Key key, Algorithm algorithm, Resource inResource, File file)
            throws Exception{

        decrypt(key, algorithm, inResource,new FileOutputStream( file ) );
    }

    public static void decrypt(Key key, Algorithm algorithm, Resource inResource, OutputStream outputStream)
            throws Exception{

        Cipher cipher = Cipher.getInstance( algorithm.algorithm );
        cipher.init( Cipher.DECRYPT_MODE, key );

        copyStreamWithCipher(inResource.getInputStream(),outputStream,cipher);
    }

    public static File decrypt(Key key, Algorithm algorithm, Resource inResource)throws Exception{
        File file = new File( System.getProperty( "java.io.tmpdir" ) , UUID.randomUUID().toString() );
        if(!file.exists() && file.createNewFile()){
            LOG.info( "创建临时文件,",file.getAbsolutePath() );
        }
        decrypt(key, algorithm, inResource, new FileOutputStream( file ) );
        return file;
    }


    public static byte[] decrypt(Key key, Algorithm algorithm, byte[] cipherContent) throws Exception {
        Cipher cipher = Cipher.getInstance( algorithm.algorithm );
        cipher.init( Cipher.DECRYPT_MODE, key );

        return cipher.doFinal( cipherContent );
    }


    public static byte[] decrypt(String keyBase64, Algorithm algorithm, byte[] cipherContent) throws Exception {
        Key key = KeyUtils.readAesKey( keyBase64 );

        return decrypt( key, algorithm, cipherContent );
    }

    /**
     * @param cipherText 密文(base64编码), not null
     * @return 明文
     */
    public static String decrypt(String keyBase64, Algorithm algorithm, String cipherText) throws Exception {

        byte[] decryptByte = decrypt( keyBase64, algorithm, Base64.decodeBase64( cipherText ) );
        return new String( decryptByte, StandardCharsets.UTF_8 );
    }


    public static byte[] decryptWithRsaPublic(String keyBase64, Algorithm algorithm, byte[] cipherContent) throws Exception {

        Key key = KeyUtils.readRsaPublicKey( keyBase64 );

        return decrypt( key, algorithm, cipherContent );
    }

    /**
     * @param cipherText 密文(base64编码), not null
     * @return 明文
     */
    public static String decryptWithRsaPublic(String keyBase64, Algorithm algorithm, String cipherText) throws Exception {

        byte[] decryptByte = decryptWithRsaPublic( keyBase64, algorithm, Base64.decodeBase64( cipherText ) );
        return new String( decryptByte, StandardCharsets.UTF_8 );
    }

    public static byte[] decryptWithRsaPrivate(String keyBase64, Algorithm algorithm, byte[] cipherContent) throws Exception {

        Key key = KeyUtils.readRsaPrivateKey( keyBase64 );

        return decrypt( key, algorithm, cipherContent );
    }

    /**
     * @param cipherText 密文(base64编码), not null
     * @return 明文
     */
    public static String decryptWithRsaPrivate(String keyBase64, Algorithm algorithm, String cipherText) throws Exception {

        byte[] decryptByte = decryptWithRsaPrivate( keyBase64, algorithm, Base64.decodeBase64( cipherText ) );
        return new String( decryptByte, StandardCharsets.UTF_8 );
    }


    /*------------------------------------------------  以下是加密 -------------------------------------------------------------------------*/

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
    public static String encryptText(Key key, String plaintext) throws Exception {
        byte[] encryptByte = encrypt( key, plaintext.getBytes( StandardCharsets.UTF_8 ) );
        return Base64.encodeBase64String( encryptByte );

    }

    /**
     * @return 密文字节
     */
    public static byte[] encrypt(Key key, byte[] plainTextContent) throws Exception {
        Cipher cipher = Cipher.getInstance( key.getAlgorithm() );
        cipher.init( Cipher.ENCRYPT_MODE, key );

        return cipher.doFinal( plainTextContent );
    }


    public static void encrypt(Key key, Algorithm algorithm, Resource inResource, OutputStream outputStream)
            throws Exception{

        Cipher cipher = Cipher.getInstance( algorithm.algorithm );
        cipher.init( Cipher.ENCRYPT_MODE, key );

        copyStreamWithCipher(inResource.getInputStream(),outputStream,cipher);
    }

    public static void encrypt(Key key, Algorithm algorithm, Resource inResource, File file)
            throws Exception{
        encrypt(key, algorithm, inResource, new FileOutputStream( file ) );
    }

    public static File encrypt(Key key, Algorithm algorithm, Resource inResource)throws Exception{
        File file = new File( System.getProperty( "java.io.tmpdir" ) , UUID.randomUUID().toString() );
        if(!file.exists() && file.createNewFile()){
            LOG.info( "创建临时文件,",file.getAbsolutePath() );
        }
        encrypt(key, algorithm, inResource, new FileOutputStream( file ) );
        return file;
    }




    /**
     * @return 密文字节
     */
    public static byte[] encrypt(Key key, Algorithm algorithm, byte[] plainTextContent) throws Exception {
        Cipher cipher = Cipher.getInstance( algorithm.algorithm );
        cipher.init( Cipher.ENCRYPT_MODE, key );

        return cipher.doFinal( plainTextContent );
    }

    public static byte[] encrypt(String keyBase64, Algorithm algorithm, byte[] plainTextContent) throws Exception {

        Key key = KeyUtils.readAesKey( keyBase64 );

        return encrypt( key, algorithm, plainTextContent );
    }


    public static String encrypt(String keyBase64, Algorithm algorithm, String plaintext) throws Exception {

        byte[] encryptByte = encrypt( keyBase64, algorithm, plaintext.getBytes( StandardCharsets.UTF_8 ) );
        return Base64.encodeBase64String( encryptByte );
    }


    public static byte[] encryptWithRsaPublic(String keyBase64, Algorithm algorithm, byte[] plainTextContent) throws Exception {

        Key key = KeyUtils.readRsaPublicKey( keyBase64 );

        return encrypt( key, algorithm, plainTextContent );
    }

    public static String encryptWithRsaPublic(String keyBase64, Algorithm algorithm, String plaintext) throws Exception {

        byte[] encryptByte = encryptWithRsaPublic( keyBase64, algorithm, plaintext.getBytes( StandardCharsets.UTF_8 ) );
        return Base64.encodeBase64String( encryptByte );
    }

    public static byte[] encryptWithRsaPrivate(String keyBase64, Algorithm algorithm, byte[] plainTextContent) throws Exception {
        Key key = KeyUtils.readRsaPrivateKey( keyBase64 );

        return encrypt( key, algorithm, plainTextContent );
    }

    public static String encryptWithRsaPrivate(String keyBase64, Algorithm algorithm, String plaintext) throws Exception {

        byte[] encryptByte = encryptWithRsaPrivate( keyBase64, algorithm, plaintext.getBytes( StandardCharsets.UTF_8 ) );
        return Base64.encodeBase64String( encryptByte );
    }

    private static void copyStreamWithCipher(InputStream inputStream, OutputStream outputStream, Cipher cipher)
            throws Exception{
        try(InputStream in = inputStream; OutputStream out = outputStream) {
            byte[] buffer = new byte[2048],outBuffer;
            int len;
            for(;(len = in.read(buffer)) > 0;){
                outBuffer =  cipher.update( buffer,0,len );
                if(outBuffer != null){
                    out.write( outBuffer );
                }
            }
            outBuffer =  cipher.doFinal();
            if(outBuffer != null){
                out.write( outBuffer );
            }
        }
    }


}
