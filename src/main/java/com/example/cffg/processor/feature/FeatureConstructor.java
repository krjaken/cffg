package com.example.cffg.processor.feature;

import com.example.cffg.vaadin.models.FeatureDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;

@Slf4j
@Service
public class FeatureConstructor {
    BufferedReader reader;

    public FeatureDto build(Path featurePath) {
        String[] stringPath = featurePath.toString().split("/");

        if (isFeatureFile(stringPath)) {
            try {
                reader = new BufferedReader(new FileReader(featurePath.toString()));
                String line = reader.readLine();
                while (line != null) {
                    System.out.println(line);
                    line = reader.readLine();
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }

        return null;
    }

    private boolean isFeatureFile(String[] stringPath) {
        return stringPath.length > 0 && stringPath[stringPath.length - 1].contains(".feature");
    }

}
