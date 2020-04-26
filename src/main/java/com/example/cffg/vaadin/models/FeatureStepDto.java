package com.example.cffg.vaadin.models;

import com.example.cffg.vaadin.enums.Actions;
import lombok.Data;

@Data
public class FeatureStepDto {
    private Actions action;
    private String stepDescription;

    @Override
    public String toString(){
        return "";
    }
}
