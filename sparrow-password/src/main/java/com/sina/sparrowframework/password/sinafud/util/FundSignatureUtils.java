package com.sina.sparrowframework.password.sinafud.util;

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

public abstract class FundSignatureUtils {

    private static final Logger LOG = LoggerFactory.getLogger(FundSignatureUtils.class);

    public static final String SIGN = "sign";

    public static final String DATA = "data";

    public static final String CODE_NODE = "/code";

    public static final String MESSAGE_NODE = "/msg";

    public static final String DATA_NODE = "/data";
    public static final String SIGN_NODE = "/sign";


    public static String fundSignature(String Json) {
        String signatureContent = sortSignatureContent(Json);
        LOG.info("FundSignatureUtils signatureContent = {}",signatureContent);
        return SignatureUtils.signatureWithRSA1(signatureContent, FundKeyManager.businessPlatformPrivateKey);
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
            Map<String, Object> signMap = new HashMap<>();
            if (jsonNode.isObject()) {
                signMap = JacksonUtil.jsonToMap(jsonNode.toString());
                signMap.entrySet().removeIf(entry -> entry.getValue() == null || "".equals(entry.getValue()));
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
