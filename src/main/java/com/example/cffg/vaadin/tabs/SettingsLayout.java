package com.example.cffg.vaadin.tabs;

import com.example.cffg.processor.CucumberProcessor;
import com.example.cffg.vaadin.TabConstructor;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;

public class SettingsLayout extends CffgTab {
    private Div contentLayout;
    private Button reloadRepo;

    public SettingsLayout(CucumberProcessor cucumberProcessor, String label, TabConstructor tabConstructor) {
        super(cucumberProcessor, label, tabConstructor);
        init();
    }

    private void init() {
        contentLayout = new Div();
        reloadRepo = new Button("Reload TEST repository");
        reloadRepo.addClickListener(buttonClickEvent -> {
            try {
                cucumberProcessor.updateRepo();
            }catch (Exception e){
                Notification.show(e.getMessage());
            }
            Notification.show("Repository updated");
        });

        contentLayout.add(reloadRepo);
        contentLayout.setVisible(false);
        tabConstructor.setComponent(this, contentLayout);
    }
}
