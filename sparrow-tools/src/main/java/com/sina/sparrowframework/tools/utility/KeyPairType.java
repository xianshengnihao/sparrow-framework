package com.sina.sparrowframework.tools.utility;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * created  on 2019-03-13.
 */
public enum KeyPairType  {

    RSA(200, "RSA",  Arrays.asList(1024, 2048, 4096));


    private final int code;

    private final String display;

    public final Collection<Integer> keySizes;



    KeyPairType(int code,  String display,  Collection<Integer> keySizes) {
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
