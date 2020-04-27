package com.example.cffg.processor.cucumber;

import com.fasterxml.jackson.databind.ObjectMapper;
import cucumber.api.StepDefinitionReporter;
import cucumber.runtime.RuntimeGlue;
import cucumber.runtime.StepDefinition;
import cucumber.runtime.UndefinedStepsTracker;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.java.JavaBackend;
import cucumber.runtime.xstream.LocalizedXStreams;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
class CucumberGlueBuilder {

    private ClassLoader classLoader;
    private ResourceLoader resourceLoader;

    CucumberGlueBuilder(ClassLoader classLoader, ResourceLoader resourceLoader) {
        this.classLoader = classLoader;
        this.resourceLoader = resourceLoader;
    }

    public void readGlue(List<String> gluePathList) {
        RuntimeGlue runtimeGlue = new RuntimeGlue(new UndefinedStepsTracker(), new LocalizedXStreams(classLoader));
        JavaBackend javaBackend = new JavaBackend(resourceLoader);
        javaBackend.loadGlue(runtimeGlue, gluePathList);
        List<StepDefinition> list = new ArrayList();
        runtimeGlue.reportStepDefinitions(new StepDefinitionReporter() {
            @Override
            public void stepDefinition(StepDefinition stepDefinition) {
                list.add(stepDefinition);
            }
        });
    }

    private void printObject(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            log.info(objectMapper.writeValueAsString(object));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
