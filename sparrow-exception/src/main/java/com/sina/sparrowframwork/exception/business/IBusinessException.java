package com.sina.sparrowframwork.exception.business;

import com.sina.sparrowframework.tools.struct.ResultCode;

/**
 * 本项目的所有业务异常的标志性接口.
 * 业务异常包括两类
 * <ul>
 * <li>运行时业务异常</li>
 * <li>强制性捕捉异常</li>
 * </ul>
 * created  on 02/03/2018.
 */
public interface IBusinessException {

    ResultCode getResultCode();

    String getMessage();

    Throwable getCause();

    static String createMessage(String format, Object... args) {
        String msg;
        if (format != null && args != null && args.length > 0) {
            msg = String.format( format, args );
        } else {
            msg = format;
        }
        return msg;
    }

}
