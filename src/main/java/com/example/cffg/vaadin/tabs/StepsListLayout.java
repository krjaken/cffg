package com.example.cffg.vaadin.tabs;

import com.example.cffg.processor.CucumberProcessor;
import com.example.cffg.processor.cucumber.GlueModelDto;
import com.example.cffg.vaadin.TabConstructor;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
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
        implementedStepsGrid.addColumn(glueModelDto -> {
            Annotation step = glueModelDto.getStep();
            try {
                Method method = step.getClass().getDeclaredMethod("value");
                method.setAccessible(true);
                Object invoke = method.invoke(null);
                return invoke.toString();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            String[] split = step.toString().split("value=\"");
            return split[split.length - 1].replace("\")", "");
        }).setHeader("Implemented Gherkin Step");
        implementedStepsGrid.addColumn(glueModelDto -> {
            Annotation step = glueModelDto.getStep();
            return step.annotationType();
        }).setHeader("Implementation Type");
        implementedStepsGrid.addColumn(glueModelDto -> {
            StringBuilder value = new StringBuilder();
            for (Map.Entry<String, Class<?>> entry : glueModelDto.getStepMethodParameters().entrySet()) {
                value.append(entry.getKey()).append(": ").append(entry.getValue().getName()).append("\n");
            }
            return value.toString();
        }).setHeader("Waiting Parameters");
        implementedStepsGrid.addColumn(GlueModelDto::isHook).setHeader("Is Hook");

        implementedStepsGrid.setItems(cucumberProcessor.getStepsDtos());
    }
}
