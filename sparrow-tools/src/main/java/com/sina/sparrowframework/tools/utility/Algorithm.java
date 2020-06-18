package com.sina.sparrowframework.tools.utility;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * created  on 23/01/2018.
 */
public enum Algorithm {

    AES( "AES", Collections.unmodifiableList( Collections.singletonList( 128 ) ) ),

    RSA( "RSA", Collections.unmodifiableList( Arrays.asList( 1024, 2048, 4096 ) ) );

    public final String algorithmName;

    public final Collection<Integer> keySizes;

    Algorithm(String algorithmName, Collection<Integer> keySizes) {
        this.algorithmName = algorithmName;
        this.keySizes = keySizes;
    }

}
