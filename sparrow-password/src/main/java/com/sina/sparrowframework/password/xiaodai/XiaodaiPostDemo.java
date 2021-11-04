package com.sina.sparrowframework.password.xiaodai;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sina.sparrowframework.password.wjmiddle.WjMiddleServiceClient;
import com.sina.sparrowframework.password.xiaodai.support.XiaodaiSupportBaseHttpClient;
import com.sina.sparrowframework.tools.utility.JacksonUtil;
import com.sina.sparrowframework.tools.utility.JsonUtils;

/**
 * @author 丛前 大黄蜂 Bee
 * @since 2021/11/4
 */
public class XiaodaiPostDemo {
    private static final Logger logger = LoggerFactory.getLogger(WjMiddleServiceClient.class);

    private static final XiaodaiSupportBaseHttpClient BASE_HTTP_CLIENT = new XiaodaiSupportBaseHttpClient();

    public <Q, R > R executePost(String url, Q qo, Class<R> roClass) {
        long startTimestamp = System.currentTimeMillis();
        String jsonQuery = JsonUtils.writeToJson(qo);
        try {
            logger.info(String.format("POST url: %s query: %s start", url, jsonQuery));
            Map<String, Object> params = JacksonUtil.objectToMap(qo);
            Map<String, String> headers = new HashMap<>();
            String responseStr = BASE_HTTP_CLIENT.postFormBody(url, params, headers, chooseRequestId());
            R response = JsonUtils.readFromJson(responseStr, roClass);
            logger.info(String.format("POST url: %s query: %s end. response:%s consuming:%s", url, jsonQuery, JsonUtils.writeToJson(response), System.currentTimeMillis() - startTimestamp));
            return response;
        } catch (Exception e) {
            logger.warn(String.format("POST %s %s fail. consuming: %s", url, jsonQuery, System.currentTimeMillis() - startTimestamp), e);
            throw e;
        }
    }


    private String chooseRequestId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}
