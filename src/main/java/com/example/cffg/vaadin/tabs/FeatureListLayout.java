package com.example.cffg.vaadin.tabs;

import com.example.cffg.processor.CucumberProcessor;
import com.example.cffg.vaadin.TabConstructor;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import io.cucumber.core.gherkin.Feature;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

@Slf4j
public class FeatureListLayout extends CffgTab {

    private Div contentLayout;
    private Button addBtn;
    private Grid<Pair<String, Feature>> cucumberFeatureGrid;
    private TextArea featureShow;

    public FeatureListLayout(CucumberProcessor cucumberProcessor, String label, TabConstructor tabConstructor) {
        super(cucumberProcessor, label, tabConstructor);
        init();
    }

    private void init() {
        contentLayout = new Div();
        addBtn = new Button("Add new *.feature");
        addBtn.addClickListener(buttonClickEvent -> addNewFeature());
        cucumberFeatureGrid = new Grid<>();
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
            return cucumberFeature;
        }).setHeader("Language");
        cucumberFeatureGrid.addColumn(pair -> pair.getRight().getSource()).setHeader("File Name");
        cucumberFeatureGrid.addColumn(pair -> pair.getRight().getName()).setHeader("Function Name");
        cucumberFeatureGrid.addColumn(pair -> pair.getRight().getSource()).setHeader("Function Description");
        cucumberFeatureGrid.addColumn(pair -> pair.getRight().getSource()).setHeader("Function Id");
//        cucumberFeatureGrid.addColumn(cucumberFeature -> {
//            return cucumberFeature.getFeatureElements().size();
//        }).setHeader("Count os elements");
        cucumberFeatureGrid.addComponentColumn(cucumberFeature -> {
            Button editBtn = new Button("Edit");
            editBtn.addClickListener(buttonClickEvent -> {
                log.info("Edit *.feature");
                //todo add edit feature logic
            });
            return editBtn;
        }).setHeader("Function Description");
        cucumberFeatureGrid.setItems(cucumberProcessor.getFeatureDtoList());
        cucumberFeatureGrid.addItemClickListener(event -> featureShow.setValue(event.getItem().getLeft()));
    }

    private void addNewFeature() {
        log.info("Add new *.feature");
        //todo add feature logic
    }

    public Component getContent() {
        return contentLayout;
    }
}
