package com.example.cffg.vaadin.tabs;

import com.example.cffg.processor.CucumberProcessor;
import com.example.cffg.vaadin.TabConstructor;
import com.vaadin.flow.component.html.Div;

public class FeatureBuildLayout extends CffgTab {

    public FeatureBuildLayout(CucumberProcessor cucumberProcessor, String label, TabConstructor tabConstructor) {
        super(cucumberProcessor, label, tabConstructor);
        Div component = new Div();
        component.setText(label);
        component.setVisible(false);
        tabConstructor.setComponent(this, component);
    }
}
