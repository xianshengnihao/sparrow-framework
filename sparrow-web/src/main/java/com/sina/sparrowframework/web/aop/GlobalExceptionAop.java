package com.sina.sparrowframework.web.aop;

import com.sina.sparrowframework.exception.business.BizFailException;
import com.sina.sparrowframework.exception.business.BizFailIgnoreLogException;
import com.sina.sparrowframework.metadata.ResponseResult;
import com.sina.sparrowframework.metadata.constants.BaseCode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.util.List;
import java.util.Set;

import static com.sina.sparrowframework.metadata.constants.BaseCode.*;
import static com.sina.sparrowframework.tools.utility.StrPool.COMMA;
import static com.sina.sparrowframework.tools.utility.StrPool.DOT;

@RestControllerAdvice
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionAop {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(value = Exception.class)
    public ResponseResult<Object> handleException(Exception e) {
        log.error("系统内部异常，异常信息：{}", e.getMessage(), e);
        return ResponseResult.error(BaseCode.UNKNOWN_ERROR.getCode(), "未知错误");
    }

    /**
     * 业务异常用来中断处理程序，是正常的逻辑，所以使用info级别，并且不打印堆栈信息
     */
    @ExceptionHandler(value = BizFailException.class)
    public ResponseResult<Object> handleParamsInvalidException(BizFailException e) {
        log.info("业务异常，异常信息：{}", e.getMessage());
        return ResponseResult.error(e.getErrorCode().getCode(), e.getMessage());
    }
    @ExceptionHandler(value = BizFailIgnoreLogException.class)
    public ResponseResult<Object> handleParamsInvalidException(BizFailIgnoreLogException e) {
        return ResponseResult.error(e.getErrorCode().getCode(), e.getMessage());
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseResult<Object> handleIllegalArgumentExceptionException(IllegalArgumentException e) {
        log.error("断言错误，异常信息：{}", e.getMessage(), e);
        return ResponseResult.error(ASSERT_ERROR.getCode(), e.getMessage());
    }

    /**
     * 统一处理请求参数校验(实体对象传参)
     *
     * @param e BindException
     * @return FebsResponse
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseResult<Object> validExceptionHandler(MethodArgumentNotValidException e) {
        StringBuilder message = new StringBuilder();
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        for (FieldError error : fieldErrors) {
            message.append(error.getField()).append(error.getDefaultMessage()).append(COMMA);
        }
        message = new StringBuilder(message.substring(0, message.length() - 1));
        return ResponseResult.error(BAD_REQUEST.getCode(), message.toString());
    }

    /**
     * 经过集成测试的程序，前端传递的字段类型一定是正确的，不正确的情况通常是恶意的请求，所以使用info级别记录即可，不需要打印堆栈信息
     */
    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseResult<Object> requestNotReadable(HttpMessageNotReadableException nrEx) {
        log.info("字段类型不匹配，异常信息：{}", nrEx.getMessage());
        return ResponseResult.error(BAD_REQUEST.getCode(), "字段类型不匹配");
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseResult<Object> handlerNoFoundException(Exception e) {
        log.error("路径不存在，异常信息：{}", e.getMessage(), e);
        return ResponseResult.error(NOT_FOUND.getCode(), "路径不存在，请检查路径是否正确");
    }

    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public ResponseResult<Object> requestMethodNotSupported(HttpRequestMethodNotSupportedException nsEx) {
        log.error("请求方法不允许，异常信息：{}", nsEx.getMessage(), nsEx);
        return ResponseResult.error(METHOD_NOT_SUPPORTED.getCode(), "方法不被允许");
    }

    @ExceptionHandler({HttpMediaTypeNotAcceptableException.class})
    public ResponseResult<Object> httpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException mtEx) {
        log.error("请求资源不可访问，异常信息：{}", mtEx.getMessage(), mtEx);
        return ResponseResult.error(MEDIA_TYPE_NOT_ACCEPTABLE.getCode(), "请求资源不可访问");
    }

    @ExceptionHandler({HttpMediaTypeNotSupportedException.class})
    public ResponseResult<Object> mediaTypeNotSupported(HttpMediaTypeNotSupportedException mtEx) {
        log.error("媒体类型不支持，异常信息：{}", mtEx.getMessage(), mtEx);
        return ResponseResult.error(HTTP_MEDIA_TYPE_NOT_SUPPORTED.getCode(), "不支持的媒体类型");
    }

    /**
     * 统一处理请求参数校验(普通传参)
     *
     * @param e ConstraintViolationException
     * @return FebsResponse
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseResult<Object> handleConstraintViolationException(ConstraintViolationException e) {
        StringBuilder message = new StringBuilder();
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            Path path = violation.getPropertyPath();
            String[] pathArr = StringUtils.splitByWholeSeparatorPreserveAllTokens(path.toString(), DOT);
            message.append(pathArr[1]).append(violation.getMessage()).append(COMMA);
        }
        message = new StringBuilder(message.substring(0, message.length() - 1));
        return ResponseResult.error(BAD_REQUEST.getCode(), message.toString());
    }

}
