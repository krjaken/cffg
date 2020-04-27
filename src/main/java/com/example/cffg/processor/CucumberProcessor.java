package com.example.cffg.processor;

import com.example.cffg.processor.cucumber.CucumberRuntimeProcessor;
import cucumber.runtime.model.CucumberFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

        repositoryApiService.readRepo();
    }

    public List<CucumberFeature> getFeatureDtos() {
        Optional<List<CucumberFeature>> cucumberFeatures = cucumberRuntimeProcessor.readFeature();
        return cucumberFeatures.orElseGet(ArrayList::new);
    }

    public void getStepsDtos(){
        cucumberRuntimeProcessor.readSteps();
    }

}
