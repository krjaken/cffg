package com.example.cffg.processor.cucumber;

import com.example.cffg.processor.Config;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.notification.Notification;
import io.cucumber.core.gherkin.Feature;
import io.cucumber.core.gherkin.vintage.GherkinVintageFeatureParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class CucumberRuntimeProcessor {
    private Config config;
    private ClassLoader classLoader;
    private ResourceLoader resourceLoader;
    private CucumberGlueBuilder cucumberGlueBuilder;
    private String defaultPath;
    private List<Pair<String, Feature>> features = new ArrayList<>();

    public CucumberRuntimeProcessor(Config config) {
        this.config = config;
        defaultPath = config.getProperty("DEFAULT_TEMP_PATH");
        initRuntimeOptions();
        cucumberGlueBuilder = new CucumberGlueBuilder(config);
    }

    private void initRuntimeOptions() {
        classLoader = this.getClass().getClassLoader();
        resourceLoader = null;
    }

    public List<Pair<String, Feature>> readFeature() {
        File featureFolder = new File(defaultPath + "/" + config.getProperty("CUCUMBER_PROJECT_FEATURE_REPOSITORY_PATH"));
        if (featureFolder.isDirectory()) {
            GherkinVintageFeatureParser parser = new GherkinVintageFeatureParser();
            readFeatures(parser, featureFolder);
            printObject(features);
        } else {
            Notification.show(defaultPath + "/" + config.getProperty("CUCUMBER_PROJECT_FEATURE_REPOSITORY_PATH") + "is not a folder");
        }
        return features;
    }

    public List<GlueModelDto> readSteps() {

        cucumberGlueBuilder.readGlue(Collections.singletonList(defaultPath + "/" + config.getProperty("CUCUMBER_PROJECT_GLUE_REPOSITORY_PATH")));

        return cucumberGlueBuilder.getGlueModelDtoList();
    }

    private void readFeatures(GherkinVintageFeatureParser parser, File folder) {
        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            if (fileEntry.isDirectory()) {
                readFeatures(parser, fileEntry);
            } else {
                String fileEntryName = fileEntry.getName();
                if (fileEntryName.endsWith(".feature")) {
                    String featureFileContent = readFeatureFile(fileEntry);
                    if (featureFileContent != null) {
                        Optional<Feature> optionalFeature = parser.parse(fileEntry.toURI(), featureFileContent, null);
                        optionalFeature.ifPresent(feature -> features.add(Pair.of(featureFileContent, feature)));
                    }
                } else {
                    log.warn("file: '" + fileEntryName + "' is not a *.feature file");
                }

            }
        }
    }

    private String readFeatureFile(File file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;
            String ls = System.getProperty("line.separator");
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            reader.close();

            return stringBuilder.toString();
        } catch (Exception e) {
            showError(e);
        }
        return null;
    }

    private void printObject(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            log.info(objectMapper.writeValueAsString(object));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void showError(Exception e) {
        log.error(e.getMessage());
        Notification.show(e.getMessage());
    }
}
