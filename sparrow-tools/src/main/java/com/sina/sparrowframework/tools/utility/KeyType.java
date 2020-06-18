package com.sina.sparrowframework.tools.utility;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * created  on 2019-03-13.
 * @see KeyUtils
 */
public enum KeyType {

    AES(100, "AES", Arrays.asList(128) )

   ;


    private final int code;

    private final String display;

    private final Collection<Integer> keySizes;

    KeyType(int code,  String display, Collection<Integer> keySizes) {
        this.code = code;
        this.display = display;
        this.keySizes = Collections.unmodifiableCollection(keySizes);
    }

    public int getCode() {
        return code;
    }

    public String getDisplay() {
        return display;
    }}
