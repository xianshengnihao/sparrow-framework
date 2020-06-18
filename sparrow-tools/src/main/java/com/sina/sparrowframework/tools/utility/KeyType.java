package com.sina.sparrowframework.tools.utility;

import com.sina.sparrowframework.tools.struct.CodeEnum;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * created  on 2019-03-13.
 *
 * @see KeyUtils
 */
public enum KeyType implements CodeEnum {

    AES(100, "AES", ArrayUtils.asSet(128));


    private final int code;

    private final String display;

    private final Collection<Integer> keySizes;

    private static final Map<Integer, KeyType> CODE_MAP = CodeEnum.createCodeMap(KeyType.class);


    public static KeyType resolve(int code) {
        return CODE_MAP.get(code);
    }

    KeyType(int code, String display, Collection<Integer> keySizes) {
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
