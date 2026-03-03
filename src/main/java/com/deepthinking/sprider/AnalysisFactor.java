package com.deepthinking.sprider;

import lombok.Data;

import java.util.List;

@Data
public class AnalysisFactor {

    private String modelName;
    private String xPath;
    private String results;
    private List<FeildProperty> subsets;
}
