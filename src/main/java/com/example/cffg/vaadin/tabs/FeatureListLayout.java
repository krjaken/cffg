package com.example.cffg.vaadin.tabs;

import com.example.cffg.processor.CucumberProcessor;
import com.example.cffg.vaadin.TabConstructor;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import cucumber.runtime.model.CucumberFeature;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FeatureListLayout extends CffgTab {

    private Div contentLayout;
    private Button addBtn;
    private Grid<CucumberFeature> cucumberFeatureGrid;
    private TextArea featureShow;

    public FeatureListLayout(CucumberProcessor cucumberProcessor, String label, TabConstructor tabConstructor) {
        super(cucumberProcessor, label, tabConstructor);
        init();
    }

    private void init() {
        contentLayout = new Div();
        addBtn = new Button("Add new *.feature");
        addBtn.addClickListener(buttonClickEvent -> addNewFeature());
        cucumberFeatureGrid = new Grid<>(CucumberFeature.class);
        cucumberFeatureGrid.setWidth("60%");
        fillFeatureGrid();
        featureShow = new TextArea("");
        featureShow.setWidth("30%");
        featureShow.setReadOnly(true);

        contentLayout.add(new HorizontalLayout(addBtn), new HorizontalLayout(cucumberFeatureGrid, featureShow));
        tabConstructor.setComponent(this, contentLayout);
    }

    private void fillFeatureGrid() {
        cucumberFeatureGrid.removeAllColumns();
        cucumberFeatureGrid.addColumn(cucumberFeature -> {
            return cucumberFeature.getI18n().getIsoCode();
        }).setHeader("Language");
        cucumberFeatureGrid.addColumn(CucumberFeature::getPath).setHeader("File Name");
        cucumberFeatureGrid.addColumn(cucumberFeature -> {
            return cucumberFeature.getGherkinFeature().getName();
        }).setHeader("Function Name");
        cucumberFeatureGrid.addColumn(cucumberFeature -> {
            return cucumberFeature.getGherkinFeature().getDescription();
        }).setHeader("Function Description");
        cucumberFeatureGrid.addColumn(cucumberFeature -> {
            return cucumberFeature.getGherkinFeature().getId();
        }).setHeader("Function Id");
        cucumberFeatureGrid.addColumn(cucumberFeature -> {
            return cucumberFeature.getFeatureElements().size();
        }).setHeader("Count os elements");
        cucumberFeatureGrid.addComponentColumn(cucumberFeature -> {
            Button editBtn = new Button("Edit");
            editBtn.addClickListener(buttonClickEvent -> {
                log.info("Edit *.feature");
                //todo add edit feature logic
            });
            return editBtn;
        }).setHeader("Function Description");
        cucumberFeatureGrid.setItems(cucumberProcessor.getFeatureDtoList());
        cucumberFeatureGrid.addItemClickListener(event -> {
            featureShow.setValue(cucumberProcessor.getFeatureByFileName(event.getItem().getPath()));
        });
    }

    private void addNewFeature() {
        log.info("Add new *.feature");
        //todo add feature logic
    }

    public Component getContent() {
        return contentLayout;
    }
}
