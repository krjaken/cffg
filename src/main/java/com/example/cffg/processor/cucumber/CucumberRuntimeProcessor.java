package com.example.cffg.processor.cucumber;

import com.example.cffg.processor.Config;
import com.vaadin.flow.component.notification.Notification;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.model.CucumberFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CucumberRuntimeProcessor {
    private Config config;
    private RuntimeOptions runtimeOptions;
    private ClassLoader classLoader;
    private ResourceLoader resourceLoader;
    private CucumberGlueBuilder cucumberGlueBuilder;
    private String defaultPath;

    public CucumberRuntimeProcessor(Config config) {
        this.config = config;
        defaultPath = config.getProperty("DEFAULT_TEMP_PATH");
        initRuntimeOptions();
        cucumberGlueBuilder = new CucumberGlueBuilder(classLoader, resourceLoader, config);
    }

    private void initRuntimeOptions() {
        runtimeOptions = new RuntimeOptions(buildArgv());
        classLoader = this.getClass().getClassLoader();
        resourceLoader = new MultiLoader(classLoader);
    }

    public Optional<List<CucumberFeature>> readFeature() {

        try {
            return Optional.of(runtimeOptions.cucumberFeatures(resourceLoader));
        } catch (Exception e) {
            showError(e);
        }
        return Optional.empty();
    }

    public List<GlueModelDto> readSteps() {

        cucumberGlueBuilder.readGlue(Collections.singletonList(defaultPath + "/" + config.getProperty("CUCUMBER_PROJECT_GLUE_REPOSITORY_PATH")));

        return cucumberGlueBuilder.getGlueModelDtoList();
    }

    private LinkedList<String> buildArgv() {
        LinkedList<String> argv = new LinkedList<>();
        argv.add("--snippets");
        argv.add("UNDERSCORE");
        argv.add("--glue");
        argv.add(defaultPath + "/" + config.getProperty("CUCUMBER_PROJECT_GLUE_REPOSITORY_PATH"));
        argv.add(defaultPath + "/" + config.getProperty("CUCUMBER_PROJECT_FEATURE_REPOSITORY_PATH"));
        argv.add("--plugin");
        argv.add("null");

        return argv;
    }

    private void showError(Exception e) {
        log.error(e.getMessage());
        Notification.show(e.getMessage());
    }
}
