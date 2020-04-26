package com.example.cffg.vaadin;

import com.example.cffg.processor.CucumberProcessor;
import com.example.cffg.vaadin.models.FeatureDto;
import com.example.cffg.vaadin.models.FeatureStepDto;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Route("main")
public class UiStarter extends VerticalLayout {
    private CucumberProcessor cucumberProcessor;

    public UiStarter(CucumberProcessor cucumberProcessor){
        this.cucumberProcessor = cucumberProcessor;
        Button addNewBtn = new Button("Add");
        add(addNewBtn);
        Grid<FeatureDto> grid = new Grid<>();
        grid.addColumn(FeatureDto::getFeatureName).setHeader("Action");
        grid.addColumn(FeatureDto::getScenarios).setHeader("Description");
        grid.addColumn(featureStepDto -> {return new Button("remove");}).setHeader("remove");
        grid.setItems(this.cucumberProcessor.getFeatureDtos());

        add(grid);
    }

}
