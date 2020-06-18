package com.sina.sparrowframework.tools.utility;

import com.sina.sparrowframework.tools.struct.CodeEnum;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * created  on 2019-03-13.
 */
public enum KeyPairType implements CodeEnum {

    RSA(200, "RSA", ArrayUtils.asSet(1024, 2048, 4096));


    private final int code;

    private final String display;

    public final Collection<Integer> keySizes;

    private static final Map<Integer, KeyType> CODE_MAP = CodeEnum.createCodeMap(KeyType.class);


    /**
     * Resolve the {@link KeyType} from {@link KeyPairType#code}
     *
     * @param code the {@link KeyPairType#code}
     * @return Return {@link KeyType},The result may null
     */
    public static KeyType resolve(int code) {
        return CODE_MAP.get(code);
    }

    KeyPairType(int code, String display, Collection<Integer> keySizes) {
        Assert.assertNotNull(display, "Display can not be null");
        Assert.assertTrue(keySizes != null && keySizes.size() > 0, "KeySizes can not be null");
        this.code = code;
        this.display = display;
        this.keySizes = Collections.unmodifiableCollection(keySizes);
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
