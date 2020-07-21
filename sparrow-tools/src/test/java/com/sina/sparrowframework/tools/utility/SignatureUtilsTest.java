package com.sina.sparrowframework.tools.utility;


import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import static com.sina.sparrowframework.tools.utility.KeyUtils.*;
import static com.sina.sparrowframework.tools.utility.SignatureUtils.signatureWithSM2WithSM3;
import static com.sina.sparrowframework.tools.utility.SignatureUtils.verifySignatureWithSM2WithSM3;

public class SignatureUtilsTest {

    public static void main(String[] args) throws Exception {
        KeyPair keyPair = createSM2WithSM3KeyPair();
        String publicKey = writeToString(keyPair.getPublic());
        String privateKey = writeToString(keyPair.getPrivate());
        System.out.println("公钥:" + publicKey);
        System.out.println("私钥:" + privateKey);
        verifyResult(keyPair.getPublic(), keyPair.getPrivate());

        System.out.println("============================ 从字符串中读取公私钥对象 ==========================");

        PublicKey publicKeyObject = readSM2WithSM3PublicKey(publicKey);
        PrivateKey privateKeyObject = readSM2WithSM3PrivateKey(privateKey);
        verifyResult(publicKeyObject, privateKeyObject);
    }

    private static void verifyResult(PublicKey publicKey, PrivateKey privateKey) {
        String content = "{大噶吼,我hey渣渣辉}";
        String sign = signatureWithSM2WithSM3(content, privateKey);
        System.out.println("签名后的值为:" + sign);

        boolean b = verifySignatureWithSM2WithSM3(content, sign, publicKey);
        System.out.println("验签后的结果为:" + b);
    }
}
