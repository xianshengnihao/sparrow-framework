package com.sina.sparrowframework.tools.utility;

import com.sina.sparrowframework.tools.struct.CodeEnum;
import org.bouncycastle.asn1.gm.GMObjectIdentifiers;

import java.util.Map;

/**
 * created  on 2019-03-20.
 */
public enum SignatureType implements CodeEnum {

    NONEwithRSA(10, "NONEwithRSA"),

    MD2withRSA(20, "MD2withRSA"),

    MD5withRSA(30, "MD5withRSA"),

    SHA1withRSA(40, "SHA1withRSA"),

    SHA224withRSA(50, "SHA224withRSA"),

    SHA256withRSA(60, "SHA256withRSA"),

    SHA384withRSA(70, "SHA384withRSA"),

    SHA512withRSA(80, "SHA512withRSA"),

    NONEwithDSA(90, "NONEwithDSA"),

    SHA1withDSA(100, "SHA1withDSA"),

    SHA224withDSA(110, "SHA224withDSA"),

    SHA256withDSA(120, "SHA256withDSA"),

    NONEwithECDSA(130, "NONEwithECDSA"),

    SHA1withECDSA(140, "SHA1withECDSA"),

    SHA224withECDSA(150, "SHA224withECDSA"),

    SHA256withECDSA(160, "SHA256withECDSA"),

    SHA384withECDSA(170, "SHA384withECDSA"),

    SHA512withECDSA(180, "SHA512withECDSA"),

    SM2withSM3(190, GMObjectIdentifiers.sm2sign_with_sm3.toString());


    private final int code;

    private final String display;


    private static final Map<Integer, KeyType> CODE_MAP = CodeEnum.createCodeMap(KeyType.class);


    public static KeyType resolve(int code) {
        return CODE_MAP.get(code);
    }

    SignatureType(int code, String display) {
        this.code = code;
        this.display = display;
    }

    @Override
    public int code() {
        return code;
    }

    @Override
    public String display() {
        return display;
    }


}
