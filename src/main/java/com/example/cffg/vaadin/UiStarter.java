package com.example.cffg.vaadin;

import com.example.cffg.vaadin.models.FeatureStepDto;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Route("main")
public class UiStarter extends VerticalLayout {
    public UiStarter(){
        Button addNewBtn = new Button("Add");
        add(addNewBtn);
        Grid<FeatureStepDto> grid = new Grid<>();
        grid.addColumn(FeatureStepDto::getAction).setHeader("Action");
        grid.addColumn(FeatureStepDto::getStepDescription).setHeader("Description");
        grid.addColumn(featureStepDto -> {return new Button();}).setHeader("remove");
        add(grid);
    }

}
