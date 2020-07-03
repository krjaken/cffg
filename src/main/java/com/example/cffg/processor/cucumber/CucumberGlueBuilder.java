package com.example.cffg.processor.cucumber;

import com.example.cffg.processor.Config;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.core.backend.Glue;
import io.cucumber.core.backend.Lookup;
import io.cucumber.core.eventbus.EventBus;
import io.cucumber.plugin.event.Event;
import io.cucumber.plugin.event.EventHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

@Slf4j
class CucumberGlueBuilder {

    private Config config;

    private JavaFileCompiler javaFileCompiler;
    private List<Pair<String, File>> javaFilesList = new ArrayList<>();
    private List<Glue> glueList;

    CucumberGlueBuilder(Config config) {
        this.config = config;
        javaFileCompiler = new JavaFileCompiler(config);
        glueList = new ArrayList<>();
    }

    public void readGlue(List<String> gluePathList) {

        File glueFile = new File(config.getProperty("DEFAULT_TEMP_PATH") + "/" + config.getProperty("CUCUMBER_PROJECT_GLUE_REPOSITORY_PATH"));
        try {
            String[] pathList = new String[gluePathList.size()];

            for (int i = 0; i < gluePathList.size(); i++) {
                pathList[i] = gluePathList.get(i);
            }
            readJavaFiles(pathList);

            //List<URI> glue = new ArrayList<>();
            //List<Class<?>> glueStepClass = new ArrayList<>();
            for (Pair<String, File> pair : javaFilesList) {
                Object compile = javaFileCompiler.compile(pair);

                Object glueAdapter = initGlueAdapter();
                initMethodScanner(compile.getClass(), (method, annotation) -> {
                    glueAdapterAddStep(glueAdapter, method, annotation);
                });
                printObject(glueList);
                log.info(String.valueOf(glueList.size()));
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error("ERROR: " + e.getMessage());
        }
    }

    private void glueAdapterAddStep(Object glueAdapter, Method method, Annotation annotation) {
        try {
            Method addDefinition = glueAdapter.getClass().getDeclaredMethod("addDefinition", Method.class, Annotation.class);
            addDefinition.setAccessible(true);
            addDefinition.invoke(glueAdapter, method, annotation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Object initGlueAdapter() {
        try {
            Class<?> aClass = Class.forName("io.cucumber.java.GlueAdaptor");
            Constructor<?> constructor = aClass.getDeclaredConstructor(Lookup.class, Glue.class);
            constructor.setAccessible(true);
            Object instance = constructor.newInstance(new Lookup() {
                @Override
                public <T> T getInstance(Class<T> aClass) {
                    try {
                        return aClass.newInstance();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }, initGlue());
            return instance;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Glue initGlue() {
        try {
            Class<?> aClass = Class.forName("io.cucumber.core.runner.CachingGlue");
            Constructor<?> constructor = aClass.getDeclaredConstructor(EventBus.class);
            constructor.setAccessible(true);
            Object instance = constructor.newInstance(new EventBus() {
                @Override
                public Instant getInstant() {
                    return null;
                }

                @Override
                public UUID generateId() {
                    return null;
                }

                @Override
                public void send(Event event) {

                }

                @Override
                public void sendAll(Iterable<Event> iterable) {

                }

                @Override
                public <T extends Event> void registerHandlerFor(Class<T> aClass, EventHandler<T> eventHandler) {

                }

                @Override
                public <T extends Event> void removeHandlerFor(Class<T> aClass, EventHandler<T> eventHandler) {

                }
            });

            Glue glue = (Glue) instance;
            glueList.add(glue);
            return glue;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    private void initMethodScanner(Class<?> glueClass, BiConsumer<Method, Annotation> method) {
        try {
            Class<?> aClass = Class.forName("io.cucumber.java.MethodScanner");
            Method scan = aClass.getDeclaredMethod("scan", Class.class, BiConsumer.class);
            scan.setAccessible(true);
            scan.invoke(aClass, glueClass, method);
        } catch (Exception e) {
            log.error("MethodScanner init error: " + e.getMessage());
        }

    }

    private void readJavaFiles(String[] pathList) {
        File[] files = new File[pathList.length];
        for (int i = 0; i < pathList.length; i++) {
            files[i] = new File(pathList[i]);
        }
        readFiles(files);
    }

    private void readFiles(File[] files) {
        for (File file : files) {
            if (file.isDirectory()) {
                File[] listFiles = file.listFiles();
                if (listFiles != null) {
                    readFiles(listFiles);
                }
            } else {
                if (file.getName().endsWith(".java")) {
                    javaFilesList.add(Pair.of(buildFullJavaClassName(file), file));
                }
            }
        }
    }

    private String buildFullJavaClassName(File file) {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            while (line != null) {
                String trimmedLine = line.trim();
                if (trimmedLine.startsWith("package ")) {
                    stringBuilder.append(trimmedLine.replace("package ", "")
                            .replace(";", "")).append(".");
                }
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        stringBuilder.append(file.getName().replace(".java", ""));
        return stringBuilder.toString();
    }

    private void printObject(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            log.info(objectMapper.writeValueAsString(object));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public List<GlueModelDto> getGlueModelDtoList() {
        return null;
    }
}
