package com.example.cffg.processor;

import com.example.cffg.processor.cucumber.CucumberRuntimeProcessor;
import com.example.cffg.processor.cucumber.GlueModelDto;
import cucumber.runtime.model.CucumberFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CucumberProcessor {
    private RepositoryApiService repositoryApiService;
    private Config config;
    private CucumberRuntimeProcessor cucumberRuntimeProcessor;

    public CucumberProcessor(RepositoryApiService repositoryApiService, Config config, CucumberRuntimeProcessor cucumberRuntimeProcessor) {
        this.repositoryApiService = repositoryApiService;
        this.config = config;
        this.cucumberRuntimeProcessor = cucumberRuntimeProcessor;
    }

    public void updateRepo() {
        repositoryApiService.readRepo();
    }

    public List<CucumberFeature> getFeatureDtoList() {
        Optional<List<CucumberFeature>> cucumberFeatures = cucumberRuntimeProcessor.readFeature();
        return cucumberFeatures.orElseGet(ArrayList::new);
    }

    public String getFeatureByFileName(String fileName) {
        File file = new File(config.getProperty("DEFAULT_TEMP_PATH")
                + "/" + config.getProperty("CUCUMBER_PROJECT_FEATURE_REPOSITORY_PATH") + "/" + fileName);

        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(
                    file));
            String line = reader.readLine();
            while (line != null) {
                stringBuilder.append(line).append("\n");
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return stringBuilder.toString();
    }

    public List<GlueModelDto> getStepsDtos() {
        return cucumberRuntimeProcessor.readSteps();
    }

}
