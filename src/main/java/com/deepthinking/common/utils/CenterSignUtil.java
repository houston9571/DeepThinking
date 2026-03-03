package com.deepthinking.common.utils;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Slf4j
public final class CenterSignUtil {
    private CenterSignUtil(){}

    private static final String MD5_KEY = "key";
    private static final String SIGN_KEY = "sign";

    private static final String SIGN_CONCAT_KEY = "&";

    private static final String SIGN_DATA_CONCAT_KEY = "=";

    public static <T> boolean matchSignature(T request, String secKey) {
        return matchSignature(getTreeMap(request),secKey);
    }

    public static <T> String generateSignature(T request, String secKey) {
        TreeMap<String, Object> treeMap = getTreeMap(request);
        treeMap.remove(SIGN_KEY);
        return getSign(treeMap,secKey);
    }

    private static <T> TreeMap<String, Object> getTreeMap(T request) {
        TreeMap<String, Object> treeMap = new TreeMap<>();
        try {
            Map<String,Object> beanMap = JSON.parseObject(JSON.toJSONString(request), Map.class);
            for (Object key : beanMap.keySet()) {
                treeMap.put(key.toString(), beanMap.get(key));
            }
        } catch (Exception e) {
            log.info(request.toString() + " covert to TreeMap error!");
        }
        return treeMap;
    }

    private static boolean matchSignature(TreeMap<String, Object> parameter, String secKey) {
        Object originalSignParam = parameter.remove(SIGN_KEY);
        if(Objects.isNull(originalSignParam)) {
            return false;
        }
        return StringUtils.equals(originalSignParam.toString(), getSign(parameter,secKey));
    }

    private static String getSign(TreeMap<String, Object> parameter, String secKey) {
        String newSignStr = parameter.entrySet().stream()
                .filter(item -> Objects.nonNull(item.getValue()) && StringUtils.isNotEmpty(item.getValue().toString()))
                .map(e -> e.getKey() + SIGN_DATA_CONCAT_KEY + e.getValue())
                .collect(Collectors.joining(SIGN_CONCAT_KEY));
        newSignStr = newSignStr +  SIGN_CONCAT_KEY + MD5_KEY + SIGN_DATA_CONCAT_KEY + secKey;
        log.info("getSign|进行签名|参数(newSignStr):{} ",newSignStr);
        return EncryptUtils.md5(newSignStr).toUpperCase();
    }
}
