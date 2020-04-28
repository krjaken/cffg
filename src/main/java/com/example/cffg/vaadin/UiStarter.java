package com.example.cffg.vaadin;

import com.example.cffg.processor.CucumberProcessor;
import com.example.cffg.vaadin.tabs.FeatureBuildLayout;
import com.example.cffg.vaadin.tabs.FeatureListLayout;
import com.example.cffg.vaadin.tabs.SettingsLayout;
import com.example.cffg.vaadin.tabs.StepsListLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Route("")
public class UiStarter extends Div implements TabConstructor {

    private Tabs tabs = new Tabs();
    private Map<Tab, Component> tabsToPages = new HashMap<>();
    private Div pages = new Div();
    private Set<Component> pagesShown;

    private CucumberProcessor cucumberProcessor;
    private FeatureBuildLayout featureBuildLayout;
    private StepsListLayout stepsListLayout;
    private FeatureListLayout featureListLayout;
    private SettingsLayout settingsLayout;

    public UiStarter(CucumberProcessor cucumberProcessor) {
        this.cucumberProcessor = cucumberProcessor;
        init();
        setSelectedTabListener();
    }

    private void init() {
        featureBuildLayout = new FeatureBuildLayout(cucumberProcessor, "Feature files edit", this);
        stepsListLayout = new StepsListLayout(cucumberProcessor, "Steps list", this);
        featureListLayout = new FeatureListLayout(cucumberProcessor, "Existing feature files list", this);
        settingsLayout = new SettingsLayout(cucumberProcessor, "Settings", this);
        tabs.add(featureListLayout, stepsListLayout, featureBuildLayout, settingsLayout);
        pagesShown = Stream.of(featureListLayout.getContent())
                .collect(Collectors.toSet());
        add(tabs, pages);
    }

    @Override
    public void setComponent(Tab tab, Component component) {

        tabsToPages.put(tab, component);
        pages.add(component);

    }

    private void setSelectedTabListener() {
        tabs.addSelectedChangeListener(event -> {
            pagesShown.forEach(page -> page.setVisible(false));
            pagesShown.clear();
            Component selectedPage = tabsToPages.get(tabs.getSelectedTab());
            selectedPage.setVisible(true);
            pagesShown.add(selectedPage);
        });
    }
}
