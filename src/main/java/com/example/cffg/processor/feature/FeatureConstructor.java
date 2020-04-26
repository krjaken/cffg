package com.example.cffg.processor.feature;

import com.example.cffg.vaadin.enums.Actions;
import com.example.cffg.vaadin.enums.ScenarioTypeEnum;
import com.example.cffg.vaadin.models.DataExampleDto;
import com.example.cffg.vaadin.models.FeatureDto;
import com.example.cffg.vaadin.models.FeatureStepDto;
import com.example.cffg.vaadin.models.ScenarioDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedList;

@Slf4j
@Service
public class FeatureConstructor {
    BufferedReader reader;

    public FeatureDto build(Path featurePath) {
        String[] stringPath = featurePath.toString().split("/");
        FeatureDto featureDto = null;

        if (isFeatureFile(stringPath)) {
            featureDto = new FeatureDto();
            try {
                reader = new BufferedReader(new FileReader(featurePath.toString()));
                String line = reader.readLine();
                while (line != null) {
                    analiseLine(featureDto, line);
                    line = reader.readLine();
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                return null;
            }
        }

        return featureDto;
    }

    private boolean isFeatureFile(String[] stringPath) {
        return stringPath.length > 0 && stringPath[stringPath.length - 1].contains(".feature");
    }

    private void analiseLine(FeatureDto featureDto, String line) {
        line = line.trim();

        if (line.isEmpty()){
            return;
        }

        if (line.startsWith("|") && line.endsWith("|")) {
            line = line.substring(1, line.length() - 1);
            String[] exampleArray = line.split("|");
            LinkedList<String> example = new LinkedList<>(Arrays.asList(exampleArray));

            ScenarioDto lastScenario = featureDto.getScenarios().getLast();
            if (lastScenario == null) {
                log.error("File is brocken");
            } else if (lastScenario.getExampleDto() != null) {
                lastScenario.getExampleDto().addExample(example);
            } else {
                DataExampleDto dataExampleDto = new DataExampleDto();
                dataExampleDto.addExample(example);
                lastScenario.setExampleDto(dataExampleDto);
            }
        }

        if (line.startsWith("#")) {
            featureDto.setProperties(line);
            //todo написать валидатор по языкам, сейчас юзаем русский
        } else {
            String[] words = line.split(" ");
            if (words.length > 0) {
                String first = words[0].replace(":", "");
                if ("Функция".equals(first)) {
                    featureDto.setFeatureName(arrayToString(words));
                } else {
                    //todo херня - переделать
                    for (ScenarioTypeEnum scenarioTypeEnum : ScenarioTypeEnum.values()) {
                        if (scenarioTypeEnum.toString().equals(first)) {
                            ScenarioDto scenarioDto = new ScenarioDto();
                            scenarioDto.setScenarioType(scenarioTypeEnum);
                            scenarioDto.setName(arrayToString(words));
                            featureDto.addScenario(scenarioDto);

                            break;
                        }
                    }

                    ScenarioDto lastScenario = featureDto.getScenarios().getLast();
                    for (Actions actionEnum : Actions.values()) {
                        if (actionEnum.toString().equals(first)) {
                            FeatureStepDto featureStepDto = new FeatureStepDto();
                            featureStepDto.setAction(actionEnum);
                            featureStepDto.setStepDescription(arrayToString(words));
                            lastScenario.addStep(featureStepDto);
                            break;
                        }
                    }
                }
            }
        }

    }

    private String arrayToString(String[] words) {
        String line = null;
        for (int i = 1; i < words.length; i++) {
            line = line != null ? line + " " + words[i] : words[i];
        }
        return line;
    }

}
