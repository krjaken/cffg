package com.example.cffg.processor.cucumber;

import lombok.Data;

import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class GlueModelDto {
    private String language;
    private String sourceClassName;
    private Annotation step;
    private boolean isHook = false;
    private Map<String, Class<?>> stepMethodParameters = new LinkedHashMap<>();

    public void addParameter(String parameterName, Class<?> parameterClass) {
        stepMethodParameters.put(parameterName, parameterClass);
    }
}
