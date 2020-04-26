package com.example.cffg.vaadin.models;

import com.example.cffg.vaadin.enums.ScenarioTypeEnum;
import lombok.Data;

import java.util.LinkedList;

@Data
public class ScenarioDto {

    private ScenarioTypeEnum scenarioType;
    private String name;
    private LinkedList<FeatureStepDto> steps;
    private DataExampleDto exampleDto;

    public void addStep(FeatureStepDto stepDto){
        steps.add(stepDto);
    }

    @Override
    public String toString(){
        return "";
    }
}
