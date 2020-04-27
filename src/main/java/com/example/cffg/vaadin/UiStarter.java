package com.example.cffg.vaadin;

import com.example.cffg.processor.CucumberProcessor;
import com.example.cffg.vaadin.models.FeatureDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import cucumber.runtime.model.CucumberFeature;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Route("main")
public class UiStarter extends VerticalLayout {
    private CucumberProcessor cucumberProcessor;

    public UiStarter(CucumberProcessor cucumberProcessor) {
        this.cucumberProcessor = cucumberProcessor;
        Button addNewBtn = new Button("Add");
        add(addNewBtn);
        Grid<FeatureDto> grid = new Grid<>();
        grid.addColumn(FeatureDto::getFeatureName).setHeader("Action");
        grid.addColumn(FeatureDto::getScenarios).setHeader("Description");
        grid.addColumn(featureStepDto -> {
            return new Button("remove");
        }).setHeader("remove");
        addNewBtn.addClickListener(buttonClickEvent -> {
            this.cucumberProcessor.getStepsDtos();
        });

        //grid.setItems(featureDtos);

        add(grid);
    }

}
