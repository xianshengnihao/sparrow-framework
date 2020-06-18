package com.sina.sparrowframework.tools.utility;


import java.io.File;

/**
 * created  on 2018/10/20.
 */
public abstract class SystemUtils {

    /**
     * @see OS#localOs()
     */
    public static OS localOs(){
        return OS.localOs();
    }

    public static File getTempDir(){
        return new File(System.getProperty("java.io.tmpdir"));
    }




}
