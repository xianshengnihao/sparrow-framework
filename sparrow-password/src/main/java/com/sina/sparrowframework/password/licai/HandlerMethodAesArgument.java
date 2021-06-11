package com.sina.sparrowframework.password.licai;

import com.sina.sparrowframework.exception.business.BizFailException;
import com.sina.sparrowframework.metadata.ResponseResult;
import com.sina.sparrowframework.metadata.constants.BaseCode;
import com.sina.sparrowframework.tools.utility.JacksonUtil;
import com.sina.sparrowframework.tools.utility.StrPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Conventions;
import org.springframework.core.MethodParameter;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author wxn
 * @date 2021/6/10 10:40 上午
 */
public class HandlerMethodAesArgument extends RequestResponseBodyMethodProcessor implements EnvironmentAware {
    private Logger logger = LoggerFactory.getLogger(HandlerMethodAesArgument.class);

    protected Environment environment;
    private static final ThreadLocal<String> randomKeyLocal = new ThreadLocal<>();

    public HandlerMethodAesArgument(List<HttpMessageConverter<?>> converters) {
        super(converters);
    }

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.hasParameterAnnotation(AesAccessCheck.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter
            , ModelAndViewContainer mavContainer
            , NativeWebRequest webRequest
            , WebDataBinderFactory binderFactory) throws Exception {
        parameter = parameter.nestedIfOptional();

        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        AesHttpServletRequest aesHttpServletRequest = new AesHttpServletRequest(servletRequest);
        String requestBody = aesHttpServletRequest.getRequestBody();
        Map<String, String> requestBodyMap = JacksonUtil.jsonToMap(requestBody);
        //兼容非驼峰命名
        String appId = StringUtils.isEmpty(requestBodyMap.get(AesConstant.APP_ID_NAME))
                ? requestBodyMap.get(AesConstant.APP_ID) : requestBodyMap.get(AesConstant.APP_ID_NAME);
        String appIdChannelValue
                = environment.getRequiredProperty(String.format(AesConstant.APP_ID_CHANNEL, appId));
        //检查白名单
        this.checkRequestWhiteIp(aesHttpServletRequest, appIdChannelValue);
        //加载明文数据
        String plainText = this.getPlaintext(requestBodyMap, appIdChannelValue);
        aesHttpServletRequest.setRequestBody(plainText.getBytes(StandardCharsets.UTF_8));
        ServletWebRequest servletWebRequest =
                new ServletWebRequest(
                        aesHttpServletRequest, webRequest.getNativeResponse(HttpServletResponse.class));
        Object arg = readWithMessageConverters(servletWebRequest, parameter, parameter.getNestedGenericParameterType());
        String name = Conventions.getVariableNameForParameter(parameter);

        if (binderFactory != null) {
            WebDataBinder binder = binderFactory.createBinder(webRequest, arg, name);
            if (arg != null) {
                validateIfApplicable(binder, parameter);
                if (binder.getBindingResult().hasErrors() && isBindExceptionRequired(binder, parameter)) {
                    throw new MethodArgumentNotValidException(parameter, binder.getBindingResult());
                }
            }
            if (mavContainer != null) {
                mavContainer.addAttribute(BindingResult.MODEL_KEY_PREFIX + name, binder.getBindingResult());
            }
        }

        return adaptArgumentIfNecessary(arg, parameter);
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return returnType.hasMethodAnnotation(AesResponseBody.class);

    }

    @Override
    public void handleReturnValue(Object returnValue
            , MethodParameter returnType
            , ModelAndViewContainer mavContainer
            , NativeWebRequest webRequest)
            throws IOException, HttpMediaTypeNotAcceptableException
            , HttpMessageNotWritableException {
        try {
            if (returnValue instanceof ResponseResult) {
                ResponseResult result = (ResponseResult) returnValue;
                if (result.getData() != null) {
                        returnValue = result.setData(AESUtil.encryptByAES(
                                JacksonUtil.objectToJson(result.getData()),randomKeyLocal.get()));
                }
            }
        } catch (Exception e) {
            logger.error("理财加密失败...randomKey={}",randomKeyLocal.get());
            throw new BizFailException(BaseCode.API_SIGN_ERROR,BaseCode.API_SIGN_ERROR.getDesc());
        }finally {
            randomKeyLocal.remove();
        }
        super.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
    }

    @Override
    @Autowired
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    /**
     * @param requestBodyMap 客户端请求密文数据
     * @return 解析后返回明文数据
     */
    private String getPlaintext(Map<String, String> requestBodyMap, String appIdChannelValue) {
        String result = null;
        String passWord = null;
        try {
            passWord = requestBodyMap.get(AesConstant.APP_PASSWORD);
            String data = requestBodyMap.get(AesConstant.APP_DATA);
            String appPrivateKeyValue
                    = environment.getRequiredProperty(String.format(AesConstant.APP_PRIVATE_KEY, appIdChannelValue));
            //解密 password
            String randomKey = RSASign.decryptByRSA(passWord, appPrivateKeyValue);
            result = AESUtil.decryptByAES(data, randomKey);
            randomKeyLocal.set(randomKey);
        } catch (Exception e) {
            logger.error("理财解密失败...appIdChannelValue = {} randomKey={}",appIdChannelValue,passWord);
            throw new BizFailException(BaseCode.API_SIGN_ERROR,BaseCode.API_SIGN_ERROR.getDesc());
        }
        return result;
    }


    private void checkRequestWhiteIp(HttpServletRequest request, String appIdChannelValue) {
        String requestIp = getRequestIp(request);
        logger.info("check request whiteIp ip={} ", requestIp);
        if (environment.getProperty(String.format(
                AesConstant.APP_WHITE_IPS_SWITCH, appIdChannelValue), Boolean.class, true)) {
            String ips = environment.getRequiredProperty(String.format(AesConstant.APP_WHITE_IPS, appIdChannelValue));
            Optional<String> strOp = Optional.ofNullable(ips);
            strOp.filter(
                    s -> Arrays.stream(ips.split(StrPool.COMMA))
                            .anyMatch(ipIndexStr -> ipIndexStr.equals(requestIp)))
                    .orElseThrow(() -> new BizFailException(BaseCode.IP_WHITE_LIST,BaseCode.IP_WHITE_LIST.getDesc()));
        }

    }

    private static String unknown = "unknown";

    private String getRequestIp(HttpServletRequest request) {
        if (request == null) {
            throw new RuntimeException("HTTP servlet request is null!");
        }
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        } else if (ip.indexOf(StrPool.COMMA) > 0) {
            String[] ipArr = ip.split(StrPool.COMMA);
            ip = ipArr[0];
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

}
