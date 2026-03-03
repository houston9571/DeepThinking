package com.deepthinking.sprider;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.deepthinking.common.utils.NumberUtils;
import com.deepthinking.common.utils.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
public class HTMLAnalysisor {

    /**
     * 解析html格式的网页
     * 可以使用jsoup的路径查找方式，如：div#beian>table.detailsList[0]>tbody>tr，[]表示数组下标，从0开始
     */
    JSONArray parserDoc(SpriderTemplate template, String html) {
        long start = System.currentTimeMillis();
        Document doc = Jsoup.parse(html);
        JSONArray factors = new JSONArray();
        if (doc != null) {
            Elements elements;
            List<AnalysisFactor> analysisFactorList = template.getFactors();
            for (AnalysisFactor analysisFactor : analysisFactorList) {
                JSONObject factor = new JSONObject();
                String xPath = analysisFactor.getXPath();
                if (StrUtil.isNotEmpty(xPath)) {
                    elements = selectElements(doc, analysisFactor);
                } else {
                    elements = new Elements(doc.getAllElements().get(0));
                }
                List<FeildProperty> factorSubsets = analysisFactor.getSubsets();
                JSONArray table = new JSONArray();
                if ("ONE".equalsIgnoreCase(analysisFactor.getResults())) {
                    JSONArray properties = new JSONArray();
                    for (FeildProperty feildProperty : factorSubsets) {
                        JSONObject property = new JSONObject();
                        String text = selectElement(elements, feildProperty);
                        property.put("description", feildProperty.getDescription());
                        property.put("property", feildProperty.getProperty());
                        property.put("value", text);
//                        log.info("----> {} {} ={}", feildProperty.getDescription(), feildProperty.getProperty(), text);
                        properties.add(property);
                    }
                    table.add(properties);
                    factor.put("table", table);
                } else {
                    for (Element element : elements) {
                        JSONArray properties = new JSONArray();
                        for (FeildProperty feildProperty : factorSubsets) {
                            JSONObject property = new JSONObject();
                            String text = selectElement(element, feildProperty);
                            property.put("description", feildProperty.getDescription());
                            property.put("property", feildProperty.getProperty());
                            property.put("value", text);
//                            log.debug("----> {} {} = {}", feildProperty.getDescription(), feildProperty.getProperty(), text);
                            properties.add(property);
                        }
                        table.add(properties);
                    }
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

    List<Map<String, String>[]> parserDocAsMap(SpriderTemplate template, String html) {
        long start = System.currentTimeMillis();
        Document doc = Jsoup.parse(html);
        List<Map<String, String>[]> factors = Lists.newArrayList();
        if (doc != null) {
            Elements elements;
            List<AnalysisFactor> analysisFactorList = template.getFactors();
            for (AnalysisFactor analysisFactor : analysisFactorList) {
                String xPath = analysisFactor.getXPath();
                if (StrUtil.isNotEmpty(xPath)) {
                    elements = selectElements(doc, analysisFactor);
                } else {
                    elements = new Elements(doc.getAllElements().get(0));
                }
                Map<String, String>[] table;
                List<FeildProperty> factorSubsets = analysisFactor.getSubsets();
                if ("ONE".equalsIgnoreCase(analysisFactor.getResults())) {
                    table = new HashMap[1];
                    Map<String, String> tableMap = Maps.newHashMap();
                    for (FeildProperty feildProperty : factorSubsets) {
                        String text = selectElement(elements, feildProperty);
                        tableMap.put(feildProperty.getProperty(), text);
                    }
                    table[0] = tableMap;
                } else {
                    table = new HashMap[elements.size()];
                    int i = 0;
                    for (Element element : elements) {
                        Map<String, String> tableMap = Maps.newHashMap();
                        for (FeildProperty feildProperty : factorSubsets) {
                            String text = selectElement(element, feildProperty);
                            tableMap.put(feildProperty.getProperty(), text);
                        }
                        table[i++] = tableMap;
                    }
                }
                factors.add(table);
            }
        }
        log.debug("--> 解析[{}] 耗时:{}ms", template.getDescription(), System.currentTimeMillis() - start);
        return factors;
    }

    /**
     * 从doc中查找元素
     */
    private Elements selectElements(Document doc, AnalysisFactor factor) {
        Elements es = null;
        String xPath = factor.getXPath();
        if (StrUtil.isNotEmpty(xPath)) {
            try {
                // "xPath":"div#dongchandiya[1]>table.detailsList>tr[0]td",   类似这样的path要分段查找
                Pattern pp = Pattern.compile("\\[\\d+\\]");
                Matcher m = pp.matcher(xPath);
                int end = 0;
                while (m.find()) {  //查找是否含有下标，有几个下标就循环几次获取元素  m.start()是出现的下标
                    if (es == null) {
                        es = new Elements(doc.select(xPath.substring(0, m.start())).get(Integer.parseInt(xPath.substring(m.start() + 1, m.end() - 1))));
                    } else {
                        es = new Elements(es.select(xPath.substring(end + 1, m.start())).get(Integer.parseInt(xPath.substring(m.start() + 1, m.end() - 1))));
                    }
                    end = m.end();
                }
                //最后一个标签没有下标，不会被matcher出来
                if (end > 0) {  // 有下标
                    if ((end + 1) < xPath.length())   //最后一个下标后面还有一段
                        es = es.select(xPath.substring(end + 1));
                } else //没有下标，直接查找
                    es = doc.select(xPath);
            } catch (Exception e) {
                log.error("解析异常: Property={} xPath={}", factor.getModelName(), xPath);
            }
        }
        return es;
    }

    private String selectElement(Element element, FeildProperty feildProperty) {
        return selectElement(new Elements(element), feildProperty);
    }

    /**
     * 从Elements中查找元素
     */
    private String selectElement(Elements elements, FeildProperty feildProperty) {
        String text = "";
        String xPath = feildProperty.getXPath();
        String[] xPattern = feildProperty.getXPattern();
        String xAttribute = feildProperty.getXAttribute();
        Elements es = null;
        try {
            if (StrUtil.isNotEmpty(xPath)) {
                // "xPath":"div#dongchandiya[1]>table.detailsList>tr[0]td",   类似这样的path要分段查找
                Pattern pp = Pattern.compile("\\[\\d+\\]");
                Matcher m = pp.matcher(xPath);
                int end = 0;
                while (m.find()) {  //查找是否含有下标，有几个下标就循环几次获取元素  m.start()是出现的下标
                    if (es == null) {
                        es = new Elements(elements.select(xPath.substring(0, m.start())).get(Integer.parseInt(xPath.substring(m.start() + 1, m.end() - 1))));
                    } else {
                        es = new Elements(es.select(xPath.substring(end + 1, m.start())).get(Integer.parseInt(xPath.substring(m.start() + 1, m.end() - 1))));
                    }
                    end = m.end();
                }
                //最后一个标签没有下标，不会被matcher出来
                if (end > 0) {  // 有下标
                    if ((end + 1) < xPath.length())   //最后一个下标后面还有一段
                        es = es.select(xPath.substring(end + 1));
                } else //没有下标，直接查找
                    es = elements.select(xPath);

                //查找属性
                if (StrUtil.isNotEmpty(xAttribute)) {
                    text = es.attr(xAttribute);
                } else {
                    text = es.text();
                }
            }
            //匹配表达式，如果没有表达式，直接去text
            if (ArrayUtils.isNotEmpty(xPattern))
                if (es == null)
                    text = matcher(elements.html(), xPattern);
                else
                    text = matcher(text, xPattern);
        } catch (Exception e) {
            log.error("解析异常: Property={} xPath={} xAttribute={} xPattern={}", feildProperty.getProperty(), xPath, xAttribute, xPattern);
        }
        text = StrUtil.trim(text);
        text = "-".equals(text) ? "" : text;
        return NumberUtils.accessScienceNumeric(text);
    }


    /**
     * 支持多个正则表达式
     */
    private static String matcher(String data, String[] pattern) {
        String text = "";
        data = StringUtil.replaceTab(data);
        for (int i = 0; pattern != null && i < pattern.length; i++) {
//            log.debug("html.length:{}  pattern:{}", html.length(), pattern[i]);
            String t = "";
            Pattern pp = Pattern.compile(pattern[i]);
            Matcher m = pp.matcher(data.trim());
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