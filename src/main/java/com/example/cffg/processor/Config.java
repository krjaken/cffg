package com.example.cffg.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Slf4j
@Component
public class Config extends Properties {
    public Config() {
        File globalConfigFile = new File("src/main/resources/config.properties");

        Properties globalProperties = new Properties();

        try {
            globalProperties.load(new FileInputStream(globalConfigFile));
            putAll(globalProperties);
        } catch (IOException e) {
            log.error("Error open config file.\n" + e.getMessage());
        }
    }
}
