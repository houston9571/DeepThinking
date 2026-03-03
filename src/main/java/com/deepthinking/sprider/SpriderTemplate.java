package com.deepthinking.sprider;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

@Data
public class SpriderTemplate {

    private String siteName;
    private String description;
    private String responseFormat;
    private String requestMethod;
    private String requestBody;
    private String encoding = "UTF-8";
    private boolean executeJavaScript;
    private String url;
    List<AnalysisFactor> factors = Lists.newLinkedList();
}


