package com.sina.sparrowframework.sinacloud;


import com.sina.sparrowframework.tools.struct.CodeEnum;

import java.util.Map;

/**
 * created  on 2018/10/12.
 */
public enum AcceptMode implements CodeEnum {

    PRIVATE( 0, "私有访问权限" ),

    PUBLIC( 100, "公共访问权限" );

    private static final Map<Integer, AcceptMode> CODE_MAP = CodeEnum.createCodeMap( AcceptMode.class );


    public static AcceptMode resolve(int code) {
        return CODE_MAP.get( code );
    }


    private final int code;
    private final String display;

    AcceptMode(int code, String display) {
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
