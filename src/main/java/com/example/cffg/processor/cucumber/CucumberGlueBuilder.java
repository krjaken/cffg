package com.example.cffg.processor.cucumber;

import com.example.cffg.processor.Config;
import com.fasterxml.jackson.databind.ObjectMapper;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.runtime.ClassFinder;
import cucumber.runtime.CucumberException;
import cucumber.runtime.Utils;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.io.ResourceLoaderClassFinder;
import cucumber.runtime.java.StepDefAnnotation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Slf4j
class CucumberGlueBuilder {

    private ClassFinder classFinder;
    private JavaFileCompiler javaFileCompiler;
    private List<Pair<String, File>> javaFilesList;
    private List<GlueModelDto> glueModelDtoList;

    CucumberGlueBuilder(ClassLoader classLoader, ResourceLoader resourceLoader, Config config) {
        classFinder = new ResourceLoaderClassFinder(resourceLoader, classLoader);
        javaFilesList = new ArrayList<>();
        javaFileCompiler = new JavaFileCompiler(config);
        glueModelDtoList = new LinkedList<>();
    }

    public void readGlue(List<String> gluePathList) {

        String[] pathList = new String[gluePathList.size()];

        for (int i = 0; i < gluePathList.size(); i++) {
            pathList[i] = gluePathList.get(i);
        }
        readJavaFiles(pathList);

        List<Class<?>> glueStepClass = new ArrayList<>();
        for (Pair<String, File> pair : javaFilesList) {
            Class<?> compile = javaFileCompiler.compile(pair);
            glueStepClass.add(compile);
            printObject(compile);
        }

        scan(glueStepClass);
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
            reader = new BufferedReader(new FileReader(
                    file));
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

    private void scan(List<Class<?>> classList) {
        Iterator<Class<?>> iterator = classList.iterator();
        while (iterator.hasNext()) {
            Class<?> glueCodeClass = null;
            do {
                if (!iterator.hasNext()) {
                    continue;
                }

                Class<?> next = iterator.next();
                printObject(next);
                for (glueCodeClass = next;
                     glueCodeClass != null && glueCodeClass != Object.class && !Utils.isInstantiable(glueCodeClass);
                     glueCodeClass = glueCodeClass.getSuperclass()) {
                }
            } while (glueCodeClass == null);

            Method[] var7 = glueCodeClass.getMethods();
            int var8 = var7.length;

            for (int var9 = 0; var9 < var8; ++var9) {
                Method method = var7[var9];
                this.scan(method, glueCodeClass);
            }
        }

    }

    private void scan(Method method, Class<?> glueCodeClass) {
        Iterator var4 = findCucumberAnnotationClasses().iterator();

        while (var4.hasNext()) {
            Class<? extends Annotation> cucumberAnnotationClass = (Class) var4.next();
            Annotation annotation = method.getAnnotation(cucumberAnnotationClass);
            if (annotation != null) {
                if (!method.getDeclaringClass().isAssignableFrom(glueCodeClass)) {
                    throw new CucumberException(String.format("%s isn't assignable from %s", method.getDeclaringClass(), glueCodeClass));
                }

                if (!glueCodeClass.equals(method.getDeclaringClass())) {
                    throw new CucumberException(String.format("You're not allowed to extend classes that define Step Definitions or hooks. %s extends %s", glueCodeClass, method.getDeclaringClass()));
                }
                GlueModelDto glueModelDto = new GlueModelDto();
                glueModelDto.setStep(annotation);
                if (this.isHookAnnotation(annotation)) {
                    log.info(annotation.toString());
                    glueModelDto.setHook(true);
                } else if (this.isStepdefAnnotation(annotation)) {
                    log.info(annotation.toString());
                }
                glueModelDtoList.add(glueModelDto);
            }
        }

    }

    private Collection<Class<? extends Annotation>> findCucumberAnnotationClasses() {
        return this.classFinder.getDescendants(Annotation.class, "cucumber.api");
    }

    private boolean isHookAnnotation(Annotation annotation) {
        Class<? extends Annotation> annotationClass = annotation.annotationType();
        return annotationClass.equals(Before.class) || annotationClass.equals(After.class);
    }

    private boolean isStepdefAnnotation(Annotation annotation) {
        Class<? extends Annotation> annotationClass = annotation.annotationType();
        return annotationClass.getAnnotation(StepDefAnnotation.class) != null;
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
        return glueModelDtoList;
    }
}
