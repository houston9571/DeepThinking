package com.deepthinking.sprider;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.deepthinking.common.utils.RestfulClient;
import com.deepthinking.common.utils.StringUtil;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class SpriderTemplateParser {

    @Autowired
    SpriderTemplateContainer container;

    private HTMLAnalysisor htmlAnalysisor = new HTMLAnalysisor();

    private JSONAnalysisor jsonAnalysisor = new JSONAnalysisor();

    private static WebClient webClient = null;

    public JSONArray parser(String tpl, Map<String, String> param) {
        SpriderTemplate template = container.getSpiderTemplate(tpl);
        if (param != null && param.size() > 0)
            param.forEach((k, v) -> template.setUrl(template.getUrl().replace("{" + k + "}", v)));
        String html = "";
        JSONArray factors = new JSONArray();
        try {
            html = getPage(template);
            if (StringUtil.isNotEmpty(html)) {
                if ("HTML".equalsIgnoreCase(template.getResponseFormat())) {
                    factors = htmlAnalysisor.parserDoc(template, html);
                } else {
                    factors = jsonAnalysisor.parserJson(template, html);
                }
            }
        } catch (Exception e) {
            log.error("---->获取页面错误：", e);
            JSONObject object = new JSONObject();
            object.put("status", -1);
            object.put("url", template.getUrl());
            object.put("pageInfo", html);
            factors.add(object);
        }
        return factors;
    }

    public List<Map<String, String>[]> parserAsMap(String tpl, Map<String, String> param) {
        SpriderTemplate template = container.getSpiderTemplate(tpl);
        if (param != null && param.size() > 0)
            param.forEach((k, v) -> template.setUrl(template.getUrl().replace("{" + k + "}", v)));
        String html = "";
        List<Map<String, String>[]> array = Lists.newArrayList();
        try {
            html = getPage(template);
            if (StringUtil.isNotEmpty(html)) {
                if ("HTML".equalsIgnoreCase(template.getResponseFormat())) {
                    array = htmlAnalysisor.parserDocAsMap(template, html);
                } else {
                    array = jsonAnalysisor.parserJsonAsMap(template, html);
                }
            }
        } catch (Exception e) {
            log.error("---->解析页面错误：{} ", template.getUrl(), e);
        }
        return array;
    }

    public String getPage(String url) {
        return RestfulClient.get(url, null);
    }

    public String getPage(SpriderTemplate template) throws IOException {
        log.info("---->获取页面：Description:{} Method:{} Format:{} ", template.getDescription(), template.getRequestMethod(), template.getResponseFormat());
        log.info("---->获取页面：Url:{} ", template.getUrl());
        String html = "";
        if (template.isExecuteJavaScript()) {
            if (webClient == null) {
                webClient = new WebClient(BrowserVersion.CHROME);
            }
            webClient.getOptions().setJavaScriptEnabled(true);              // 2 启动JS
            webClient.getOptions().setCssEnabled(false);                    // 3 禁用Css，可避免自动二次請求CSS进行渲染
            webClient.getOptions().setRedirectEnabled(true);                // 4 启动客戶端重定向
            webClient.getOptions().setThrowExceptionOnScriptError(false);   // 5 js运行错誤時，是否拋出异常
            webClient.getOptions().setTimeout(50000);                       // 6 设置超时
            HtmlPage htmlPage = webClient.getPage(template.getUrl());       // 获取网页
            webClient.waitForBackgroundJavaScript(10000);      // 等待JS驱动dom完成获得还原后的网页
            html = htmlPage.asXml();
        } else {
            if ("POST".equalsIgnoreCase(template.getRequestMethod())) {
                html = RestfulClient.post(template.getUrl(), null, null, template.getRequestBody());
            } else {
                html = RestfulClient.get(template.getUrl(), null, template.getEncoding());
            }
        }
        return html;
    }


}
