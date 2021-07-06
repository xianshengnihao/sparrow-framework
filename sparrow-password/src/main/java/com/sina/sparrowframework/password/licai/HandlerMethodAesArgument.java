package com.sina.sparrowframework.password.licai;

import com.sina.sparrowframework.exception.business.BizFailException;
import com.sina.sparrowframework.metadata.ResponseResult;
import com.sina.sparrowframework.metadata.constants.BaseCode;
import com.sina.sparrowframework.tools.utility.CipherUtils;
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
    private static ThreadLocal<LocalAppSecret> randomKeyLocal = new ThreadLocal<>();

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
        //检查白名单
        this.checkRequestWhiteIp(aesHttpServletRequest, getAppIdChannel(requestBodyMap));
        //加载明文数据
        String plainText = this.getPlaintext(requestBodyMap);
        logger.info("\r\n[{}渠道访问理财] url:{}\r\n请求明文参数:{}",
                getAppIdChannel(requestBodyMap)
                ,aesHttpServletRequest.getRequestUri(), plainText);
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
            HttpServletRequest httpServletRequest =
                    (HttpServletRequest)webRequest.getNativeRequest();
            logger.info("\r\n[{}渠道访问理] url:{}\r\n响应明文参数:{}",
                    randomKeyLocal.get().getAppChannel()
                    ,httpServletRequest.getRequestURI()
                    , JacksonUtil.objectToJson(returnValue));
            if (returnValue instanceof ResponseResult) {
                ResponseResult result = (ResponseResult) returnValue;
                if (result.getData() != null) {
                    returnValue = result.setData(
                            CipherUtils.encryptByAesEcbPkcs5padding(
                            JacksonUtil.objectToJson(result.getData())
                            , randomKeyLocal.get().getRandomKey()));
                }
            }
        } catch (Exception e) {
            logger.error("{}渠道访问理 理财加密失败...randomKey={}"
                    ,randomKeyLocal.get().getAppChannel()
                    , randomKeyLocal.get().randomKey);
            throw new BizFailException(BaseCode.API_SIGN_ERROR, BaseCode.API_SIGN_ERROR.getDesc());
        } finally {
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
    private String getPlaintext(Map<String, String> requestBodyMap) {
        String result;
        String passWord = null;
        try {
            passWord = requestBodyMap.get(AesConstant.APP_PASSWORD);
            String data = requestBodyMap.get(AesConstant.APP_DATA);
            String appPrivateKeyValue
                    = environment.getRequiredProperty(String.format(AesConstant.APP_PRIVATE_KEY
                    , getAppIdChannel(requestBodyMap)));
            //解密 password
            String randomKey = CipherUtils.decryptWithRsaPrivate(
                    appPrivateKeyValue
                    , CipherUtils.Algorithm.RSA
                    , passWord);
            result = CipherUtils.decryptByAesEcbPkcs5padding(data, randomKey);
            randomKeyLocal.set(
                    new LocalAppSecret(
                            getAppIdChannel(requestBodyMap),randomKey));
        } catch (Exception e) {
            logger.error("理财解密失败...appIdChannel = {} passWord={}", getAppIdChannel(requestBodyMap), passWord);
            throw new BizFailException(BaseCode.API_SIGN_ERROR, BaseCode.API_SIGN_ERROR.getDesc());
        }
        return result;
    }

    private void checkRequestWhiteIp(HttpServletRequest request, String appIdChannelValue) {
        String requestIp = AesConstant.getRequestIp(request);
        logger.info("check request whiteIp ip={} ", requestIp);
        if (environment.getProperty(String.format(
                AesConstant.APP_WHITE_IPS_SWITCH, appIdChannelValue), Boolean.class, true)) {
            String ips = environment.getRequiredProperty(String.format(AesConstant.APP_WHITE_IPS, appIdChannelValue));
            Optional.ofNullable(ips).filter(
                    s -> Arrays.stream(ips.split(StrPool.COMMA))
                            .anyMatch(ipIndexStr -> ipIndexStr.equals(requestIp)))
                    .orElseThrow(() -> new BizFailException(BaseCode.IP_WHITE_LIST, BaseCode.IP_WHITE_LIST.getDesc()));
        }

    }
    private String getAppId(Map<String,String> requestBodyMap){
        //兼容非驼峰命名
        return StringUtils.isEmpty(
                requestBodyMap.get(AesConstant.APP_ID_NAME))
                ? requestBodyMap.get(AesConstant.APP_ID) :
                requestBodyMap.get(AesConstant.APP_ID_NAME);
    }

    private String getAppIdChannel(Map<String,String> requestBodyMap) {
        return environment.getRequiredProperty(
                String.format(AesConstant.APP_ID_CHANNEL
                        , getAppId(requestBodyMap)));
    }

    private static class LocalAppSecret{
        private String appChannel;
        private String randomKey;
        public LocalAppSecret(String appChannel
                , String randomKey
                ) {
            this.appChannel = appChannel;
            this.randomKey = randomKey;
        }
        public String getAppChannel() {
            return appChannel;
        }
        public String getRandomKey() {
            return randomKey;
        }
    }
}
