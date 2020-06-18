package com.sina.sparrowframework.tools.utility;

import org.apache.commons.codec.binary.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * created  on 07/03/2018.
 */
public abstract class DigestUtils extends org.apache.commons.codec.digest.DigestUtils {


    private static final char[] HEX_CHARS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
            'f' };



    public static String md5Base64(File file) throws IOException {
        try (FileInputStream in = new FileInputStream( file )) {
            MessageDigest digest = getMd5Digest();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read( buffer )) > 0) {
                digest.update( buffer, 0, len );
            }
            return Base64.encodeBase64String( digest.digest() );
        }
    }

    public static String encodeHexString(String text){
        return encodeHexString(text.getBytes(StandardCharsets.UTF_8));
    }

    public static String encodeHexString(byte[] data){
        return new String(DigestUtils.encodeHex(data));
    }

    public static char[] encodeHex(byte[] data) {
        int l = data.length;
        char[] out = new char[l << 1];
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = HEX_CHARS[(0xF0 & data[i]) >>> 4];
            out[j++] = HEX_CHARS[0x0F & data[i]];
        }
        return out;
    }


}
