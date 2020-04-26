package com.example.cffg.vaadin.models;

import lombok.Data;

import java.util.LinkedList;

@Data
public class FeatureDto {

    private String properties;
    private String featureName;
    private LinkedList<ScenarioDto> scenarios;

    public void addScenario(ScenarioDto scenarioDto){
        scenarios.add(scenarioDto);
    }

    @Override
    public String toString(){
        return "";
    }
}
