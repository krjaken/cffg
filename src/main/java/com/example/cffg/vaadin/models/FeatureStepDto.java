package com.example.cffg.vaadin.models;

import com.example.cffg.vaadin.enums.Actions;
import lombok.Data;

@Data
public class FeatureStepDto {

    private String properties;
    private String featureName;
    private String scenario;
    private Actions action;
    private String stepDescription;
}
