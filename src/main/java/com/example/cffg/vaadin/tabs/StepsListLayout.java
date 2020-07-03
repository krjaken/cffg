package com.example.cffg.vaadin.tabs;

import com.example.cffg.processor.CucumberProcessor;
import com.example.cffg.processor.cucumber.GlueModelDto;
import com.example.cffg.vaadin.TabConstructor;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class StepsListLayout extends CffgTab {

    private Div contentLayout;
    private Grid<GlueModelDto> implementedStepsGrid;

    public StepsListLayout(CucumberProcessor cucumberProcessor, String label, TabConstructor tabConstructor) {
        super(cucumberProcessor, label, tabConstructor);
        init();
    }

    private void init() {
        contentLayout = new Div();
        implementedStepsGrid = new Grid<>(GlueModelDto.class);
        fillImplementedStepsGrid();

        contentLayout.add(implementedStepsGrid);
        contentLayout.setVisible(false);
        tabConstructor.setComponent(this, contentLayout);
    }

    private void fillImplementedStepsGrid() {
        implementedStepsGrid.removeAllColumns();
        implementedStepsGrid.addColumn(GlueModelDto::getLanguage)
                .setHeader("Language")
                .setResizable(true)
                .setSortable(true);
        implementedStepsGrid.addColumn(GlueModelDto::getSourceClassName)
                .setHeader("Source Class")
                .setResizable(true)
                .setSortable(true);
        implementedStepsGrid.addColumn(glueModelDto -> {
            Annotation step = glueModelDto.getStep();
            String[] split = step.toString().split("value=\"");
            return split[split.length - 1].replace("\")", "");
        }).setHeader("Implemented Gherkin Step").setResizable(true);
        implementedStepsGrid.addColumn(glueModelDto -> {
            Annotation step = glueModelDto.getStep();
            return step.annotationType();
        }).setHeader("Implementation Type")
                .setResizable(true)
                .setSortable(true);
        implementedStepsGrid.addColumn(glueModelDto -> {
            StringBuilder value = new StringBuilder();
            for (Map.Entry<String, Class<?>> entry : glueModelDto.getStepMethodParameters().entrySet()) {
                String typeName = entry.getValue().getTypeName();
                String[] split = typeName.split("\\.");
                typeName = split[split.length - 1];
                value.append(typeName).append(", ");
            }
            return value.toString();
        }).setHeader("Waiting Parameters").setResizable(true);
        implementedStepsGrid.addColumn(GlueModelDto::isHook).setHeader("Is Hook").setResizable(true);

        List<GlueModelDto> stepsDtos = cucumberProcessor.getStepsDtos();
        implementedStepsGrid.setItems(stepsDtos != null ? stepsDtos : new ArrayList<>());
    }
}
