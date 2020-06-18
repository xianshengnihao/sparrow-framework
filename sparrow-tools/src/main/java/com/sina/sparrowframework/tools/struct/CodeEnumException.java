package com.sina.sparrowframework.tools.struct;

/**
 * created  on 2019-04-10.
 */
public class CodeEnumException extends RuntimeException {

    private static final long serialVersionUID = -1403262631005753572L;

    public CodeEnumException(String message) {
        super(message);
    }

    public CodeEnumException(String message, Throwable cause) {
        super(message, cause);
    }
}
