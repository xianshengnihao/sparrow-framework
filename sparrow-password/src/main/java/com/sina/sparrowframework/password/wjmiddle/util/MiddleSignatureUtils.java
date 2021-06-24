package com.sina.sparrowframework.password.wjmiddle.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.sina.sparrowframework.tools.utility.JacksonUtil;
import com.sina.sparrowframework.tools.utility.SignatureUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wxn
 */
public abstract class MiddleSignatureUtils {

    private static final Logger logger = LoggerFactory.getLogger(MiddleSignatureUtils.class);


    public static String middleSignature(String signatureContent) {
        String sort = sortSignatureContent(signatureContent);
        logger.info("\r\nmiddleSignature sort={}",sort);
        return SignatureUtils.signatureWithRSA(sort
                , MiddleKeyManager.businessPartnerPrivateKey);
    }
    public static String sortSignatureContent(String json){
        StringBuilder builder = new StringBuilder();
        String signature = null;
        List<String> list = new ArrayList<String>();
        try {
            JsonNode jsonNode = JacksonUtil.parseTree(json);
            if(jsonNode.isArray()) {
                list.add(jsonNode.toString());
            }
            Map<String, Object> signMap = new HashMap<>(8);
            if (jsonNode.isObject()) {
                signMap = JacksonUtil.jsonToMap(jsonNode.toString());
                signMap.entrySet().removeIf(entry -> entry.getValue() == null);
                list.addAll(signMap.keySet());
            }
            list.sort(String::compareTo);
            for (String key : list) {
                builder.append(key);
                if (signMap.containsKey(key)) {
                    builder.append("=")
                    .append(signMap.get(key));
                }
                builder.append("&");
            }
            signature = builder.toString();
            if (!StringUtils.isEmpty(signature)) {
                signature = signature.substring(0,builder.length()-1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return signature;
    }

}
