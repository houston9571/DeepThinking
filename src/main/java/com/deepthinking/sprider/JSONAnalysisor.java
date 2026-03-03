package com.deepthinking.sprider;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.deepthinking.common.utils.NumberUtils;
import com.deepthinking.common.utils.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 解析业务逻辑处理服务
 */
@Slf4j
@Service
public class JSONAnalysisor {

    /**
     * JsonPath查找，如：content.positionResult[0].totalCount，[]表示数组下标，从0开始
     */
    JSONArray parserJson(SpriderTemplate template, String data) {
        long start = System.currentTimeMillis();
        JSONArray factors = new JSONArray();
        if (StringUtil.isNotEmpty(data)) {
            List<AnalysisFactor> analysisFactorList = template.getFactors();
            for (AnalysisFactor analysisFactor : analysisFactorList) {
                JSONObject factor = new JSONObject();
                List<FeildProperty> factorSubsets = analysisFactor.getSubsets();
                JSONArray table = new JSONArray();
                if ("ONE".equalsIgnoreCase(analysisFactor.getResults())) {
                    if (StringUtil.isNotEmpty(analysisFactor.getXPath())) {
                        Object o = JsonPath.read(data, analysisFactor.getXPath());
                        if (o instanceof String)
                            data = o.toString();
                        else
                            data = JSON.toJSONString(o);
                    }
                    JSONArray properties = new JSONArray();
                    for (FeildProperty feildProperty : factorSubsets) {
                        JSONObject property = new JSONObject();
                        String text = selectNodes(data, feildProperty);
                        property.put("description", feildProperty.getDescription());
                        property.put("property", feildProperty.getProperty());
                        property.put("value", text);
                        properties.add(property);
                    }
                    table.add(properties);
                    factor.put("table", table);
                } else {
                    Object nodes = JsonPath.read(data, StringUtil.isNotEmpty(analysisFactor.getXPath()) ? analysisFactor.getXPath() : "$..*");
                    if (nodes instanceof net.minidev.json.JSONArray) {
                        net.minidev.json.JSONArray array = (net.minidev.json.JSONArray) nodes;
                        for (Object o : array) {
                            JSONArray properties = new JSONArray();
                            for (FeildProperty feildProperty : factorSubsets) {
                                JSONObject property = new JSONObject();
                                String text = selectNodes(JSON.toJSONString(o), feildProperty);
                                property.put("description", feildProperty.getDescription());
                                property.put("property", feildProperty.getProperty());
                                property.put("value", text);
                                properties.add(property);
                            }
                            table.add(properties);
                        }
                    } else
                        log.warn("---->解析的json问题不是JSONArray，不能解析成MANY，请选择ONE。");
                    factor.put("table", table);
                }
                factor.put("modelName", analysisFactor.getModelName());
                factor.put("results", analysisFactor.getResults());
                factor.put("originalURL", template.getUrl());
                factors.add(factor);
            }
        }
        log.debug("--> 解析[{}] 耗时:{}ms", template.getDescription(), System.currentTimeMillis() - start);
        return factors;
    }

    List<Map<String, String>[]> parserJsonAsMap(SpriderTemplate template, String data) {
        long start = System.currentTimeMillis();
        List<Map<String, String>[]> factors = Lists.newArrayList();
        if (StringUtil.isNotEmpty(data)) {
            List<AnalysisFactor> analysisFactorList = template.getFactors();
            for (AnalysisFactor analysisFactor : analysisFactorList) {
                Map<String, String>[] table = new HashMap[1];
                List<FeildProperty> factorSubsets = analysisFactor.getSubsets();
                if ("ONE".equalsIgnoreCase(analysisFactor.getResults())) {
                    if (StringUtil.isNotEmpty(analysisFactor.getXPath())) {
                        Object o = JsonPath.read(data, analysisFactor.getXPath());
                        if (o instanceof String)
                            data = o.toString();
                        else
                            data = JSON.toJSONString(o);
                    }
                    Map<String, String> tableMap = Maps.newHashMap();
                    for (FeildProperty feildProperty : factorSubsets) {
                        String text = selectNodes(data, feildProperty);
                        tableMap.put(feildProperty.getProperty(), text);
                    }
                    table[0] = tableMap;
                } else {
                    Object nodes = JsonPath.read(data, StringUtil.isNotEmpty(analysisFactor.getXPath()) ? analysisFactor.getXPath() : "$.*");
                    if (nodes instanceof net.minidev.json.JSONArray) {
                        net.minidev.json.JSONArray array = (net.minidev.json.JSONArray) nodes;
                        table = new HashMap[array.size()];
                        int i = 0;
                        for (Object o : array) {
                            Map<String, String> tableMap = Maps.newHashMap();
                            for (FeildProperty feildProperty : factorSubsets) {
                                String text = selectNodes(JSON.toJSONString(o), feildProperty);
                                tableMap.put(feildProperty.getProperty(), text);
                            }
                            table[i++] = tableMap;
                        }
                    } else
                        log.warn("---->解析的json问题不是JSONArray，不能解析成MANY，请选择ONE。");
                }
                factors.add(table);
            }
        }
        log.debug("--> 解析[{}] 耗时:{}ms", template.getDescription(), System.currentTimeMillis() - start);
        return factors;
    }

    private String selectNodes(String data, FeildProperty feildProperty) {
        String xPath = feildProperty.getXPath();
        String[] xPattern = feildProperty.getXPattern();
        String text = "";
        if (StringUtil.isNotEmpty(xPath)) {
            Object o = JsonPath.read(data, xPath);
            if (o instanceof String)
                text = o.toString();
            else
                text = JSON.toJSONString(o);
            if (ArrayUtils.isNotEmpty(xPattern) && StringUtil.isNotEmpty(text))
                text = matcher(text, xPattern);
        }
        text = StringUtil.trim(text);
        text = "-".equals(text) || "--".equals(text) ? "" : text;
        return NumberUtils.accessScienceNumeric(text);
    }

    /**
     * 支持多个正则表达式
     */
    private static String matcher(String data, String[] pattern) {
        String text = "";
        for (int i = 0; pattern != null && i < pattern.length; i++) {
//            log.debug("html.length:{}  pattern:{}", html.length(), pattern[i]);
            String t = "";
            Pattern pp = Pattern.compile(pattern[i]);
            Matcher m = pp.matcher(data);
            while (m.find()) {
                //group是针对（）来说的，group（0）就是指的整个串，group（1） 指的是第一个括号里的东西，group（2）指的第二个括号里的东西。
                //如果没有括号会有异常。这就是（） 的作用。  如何没有（） 可以这样写：group()
                t += m.group(1) + " ";
            }
            data = t;
            text = t.length() == 0 ? text : t;//重置文本，以便后续表达式匹配或者返回
        }
        return text;
    }


}