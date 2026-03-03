package com.deepthinking.sprider;

import lombok.Data;

@Data
public class FeildProperty {

    private String description;
    private String property;
    private String xPath;
    private String[] xPattern;
    private String xAttribute;
}
