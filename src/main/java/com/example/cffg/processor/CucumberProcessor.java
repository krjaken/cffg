package com.example.cffg.processor;

import com.example.cffg.processor.feature.FeatureConstructor;
import com.example.cffg.vaadin.models.FeatureDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CucumberProcessor {
    private RepositoryApiService repositoryApiService;
    private Config config;
    private FeatureConstructor featureConstructor;

    private String defaultPath;
    private LinkedList<FeatureDto> featureDtos = new LinkedList<>();

    public CucumberProcessor(RepositoryApiService repositoryApiService, Config config, FeatureConstructor featureConstructor) {
        this.repositoryApiService = repositoryApiService;
        this.config = config;
        this.featureConstructor = featureConstructor;
        defaultPath = config.getProperty("DEFAULT_TEMP_PATH");
        init();
    }

    private void init() {
        repositoryApiService.readRepo();
        reconstructFeatures();

    }

    private void reconstructFeatures() {
        File featureRepositoryPath = new File(defaultPath + "/" + config.getProperty("CUCUMBER_PROJECT_FEATURE_REPOSITORY_PATH"));
        for (Path path : Objects.requireNonNull(getFilesPathByPath(featureRepositoryPath))) {
            FeatureDto build = featureConstructor.build(path);
            if (build!=null){
                featureDtos.add(build);
            }
        }

    }

    private void reconstructGlue() {

    }

    public LinkedList<FeatureDto> getFeatureDtos(){
        return featureDtos;
    }

    private LinkedList<Path> getFilesPathByPath(File filePath) {
        try {
            return Files.list(Paths.get(filePath.getPath()))
                    .filter(Files::isRegularFile).collect(Collectors.toCollection(LinkedList::new));
        } catch (IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

}
