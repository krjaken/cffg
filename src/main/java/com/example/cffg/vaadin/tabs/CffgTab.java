package com.example.cffg.vaadin.tabs;

import com.example.cffg.processor.CucumberProcessor;
import com.example.cffg.vaadin.TabConstructor;
import com.vaadin.flow.component.tabs.Tab;

public abstract class CffgTab extends Tab {
    CucumberProcessor cucumberProcessor;
    TabConstructor tabConstructor;

    public CffgTab(CucumberProcessor cucumberProcessor, String label, TabConstructor tabConstructor){
        this.cucumberProcessor = cucumberProcessor;
        this.tabConstructor = tabConstructor;
        setLabel(label);
    }
}
