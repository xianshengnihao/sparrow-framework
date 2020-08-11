package com.sina.sparrowframework.tools.utility;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;

/**
 * created  on 30/03/2018.
 *
 * @see KeyUtils
 * @see CipherUtils
 */
public abstract class SignatureUtils {

    private static final Logger LOG = LoggerFactory.getLogger(SignatureUtils.class);


    /**
     * 验证签名
     *
     * @return true 表示成功
     */
    public static boolean verifySignatureWithRSA(String content, String signature, PublicKey publicKey) {
        return verifySignature(SignatureType.SHA256withRSA, content, signature, publicKey, CodecType.BASE64);
    }

    public static boolean verifySignatureWithRSA(byte[] contentBytes, String signature, PublicKey publicKey) {
        return verifySignature(SignatureType.SHA256withRSA, contentBytes, signature, publicKey, CodecType.BASE64);
    }

    public static boolean verifySignatureWithSM2WithSM3(String content, String signature, PublicKey publicKey) {
        return verifySignature(SignatureType.SM2withSM3, content, signature, publicKey, CodecType.HEX);
    }

    public static boolean verifySignature(SignatureType name, final InputStream content, final String signature,
                                          PublicKey publicKey) {
        boolean valid;
        try {
            Signature signatureObj = Signature.getInstance(name.display());
            signatureObj.initVerify(publicKey);
            byte[] buffer = new byte[1024];
            int len;
            for (; (len = content.read(buffer)) > 0; ) {
                signatureObj.update(buffer, 0, len);
            }
            valid = signatureObj.verify(Base64.decodeBase64(signature));
        } catch (Exception e) {
            LOG.error("验证签名错误");
            valid = false;
        }
        return valid;
    }

    /**
     * 获取签名
     *
     * @return 若签名成功则返回签名结果, 否则返回 null
     */
    public static String signatureWithRSA(String content, PrivateKey privateKey) {
        return signature(SignatureType.SHA256withRSA, content, privateKey, CodecType.BASE64);
    }

    public static String signatureWithRSA(byte[] contentBytes, PrivateKey privateKey) {
        return signature(SignatureType.SHA256withRSA, contentBytes, privateKey, CodecType.BASE64);
    }

    public static String signatureWithSM2WithSM3(String content, PrivateKey privateKey) {
        return signature(SignatureType.SM2withSM3, content, privateKey, CodecType.HEX);
    }


    public static String signature(SignatureType name, String content, PrivateKey key, CodecType codecType) {
        return signature(name, content.getBytes(StandardCharsets.UTF_8), key, codecType);
    }

    public static String signature(SignatureType name, byte[] contentBytes, PrivateKey key, CodecType codecType) {
        String signature = null;
        try {
            Signature signatureObj = getSignature(name);
            signatureObj.initSign(key);
            signatureObj.update(contentBytes);
            signature = codecType.encode(signatureObj.sign());
        } catch (NoSuchAlgorithmException e) {
            LOG.error("签名验证算法错误.", e);
        } catch (InvalidKeyException e) {
            LOG.error("private key invalid ", e);
        } catch (SignatureException e) {
            LOG.error("signature exception. ", e);
        } catch (Exception e) {
            LOG.error("未知错误，请检查代码或依赖库的版本", e);
        }
        return signature;
    }

    public static String signature(SignatureType name, InputStream content, PrivateKey key) throws IOException {
        try {
            Signature signatureObj = Signature.getInstance(name.display());
            signatureObj.initSign(key);
            int len;
            byte[] buffer = new byte[1024];

            for (; (len = content.read(buffer)) > 0; ) {
                signatureObj.update(buffer, 0, len);
            }
            return Base64.encodeBase64String(signatureObj.sign());
        } catch (Exception e) {
            LOG.error("签名验证算法错误.", e);
            throw new IOException(e);
        }

    }


    /**
     * 签名并 copy in 到 out ,签名完成后关闭 in ,out 保持打开.
     *
     * @return 签名
     */
    public static String signatureAndCopy(SignatureType name, InputStream in, OutputStream out, PrivateKey key)
            throws IOException {
        try (InputStream input = in) {
            Signature signatureObj = Signature.getInstance(name.name());
            signatureObj.initSign(key);

            byte[] buffer = new byte[2048];
            int len;
            while ((len = input.read(buffer)) > 0) {
                out.write(buffer, 0, len);
                signatureObj.update(buffer, 0, len);

            }
            out.flush();
            return Base64.encodeBase64String(signatureObj.sign());
        } catch (Exception e) {
            throw new IOException(e.getMessage(), e);
        }
    }


    public static String signatureAndCopy(SignatureType name, InputStream in, File outFile, PrivateKey key)
            throws IOException {
        try (FileOutputStream out = new FileOutputStream(outFile)) {
            return signatureAndCopy(name, in, out, key);
        }
    }


    public static boolean verifySignature(SignatureType name, byte[] contentBytes, String signature, PublicKey publicKey, CodecType codecType) {
        boolean valid = false;
        try {
            Signature signatureObj = getSignature(name);
            signatureObj.initVerify(publicKey);
            signatureObj.update(contentBytes);
            valid = signatureObj.verify(codecType.decode(signature));
        } catch (NoSuchAlgorithmException e) {
            LOG.error("签名验证算法错误.", e);
        } catch (InvalidKeyException e) {
            LOG.error("public key invalid ", e);
        } catch (SignatureException e) {
            LOG.error("signature exception. ", e);
        } catch (Exception e) {
            LOG.error("", e);
        }
        return valid;
    }

    public static boolean verifySignature(SignatureType name, String content, String signature, PublicKey publicKey, CodecType codecType) {
        return verifySignature(name, content.getBytes(StandardCharsets.UTF_8), signature, publicKey, codecType);
    }

    private static Signature getSignature(SignatureType name) throws Exception {
        Signature signature;
        switch (name) {
            case SM2withSM3:
                signature = Signature.getInstance(name.display(), new BouncyCastleProvider());
                break;
            default:
                signature = Signature.getInstance(name.display());
                break;
        }
        return signature;
    }

    enum CodecType {

        BASE64 {
            @Override
            public String encode(byte[] sign) {
                return Base64.encodeBase64String(sign);
            }

            @Override
            public byte[] decode(String sign) {
                return Base64.decodeBase64(sign);
            }
        },

        HEX {
            @Override
            public String encode(byte[] sign) {
                return Hex.encodeHexString(sign);
            }

            @Override
            public byte[] decode(String sign) throws DecoderException {
                return Hex.decodeHex(sign);
            }
        };

        public abstract String encode(byte[] sign);

        public abstract byte[] decode(String sign) throws DecoderException;
    }
}
