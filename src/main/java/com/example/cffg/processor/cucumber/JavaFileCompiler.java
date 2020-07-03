package com.example.cffg.processor.cucumber;

import com.example.cffg.processor.Config;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collections;

@Slf4j
class JavaFileCompiler {

    private Config config;

    JavaFileCompiler(Config config) {
        this.config = config;
    }

    Object compile(Pair<String, File> pair) {
        File baseFile = pair.getRight();
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        Iterable<? extends JavaFileObject> compileUnit = fileManager.getJavaFileObjectsFromFiles(Collections.singleton(baseFile));
        String[] options = {"-g"};
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, Arrays.asList(options), null, compileUnit);

        boolean success = task.call();

        log.info("Success: " + success);

        if (success) {
            String className = baseFile.getPath().replace(".java", "").replace("\\", ".");
            File root = new File(config.getProperty("DEFAULT_TEMP_PATH") + "/src/test/java/");
            addPath(root);
            try {
                log.info("java file compile started: " + className);
                URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{root.toURI().toURL()});
                Class<?> aClass = Class.forName(pair.getKey(), true, classLoader);
                try {
                    Object object = aClass.newInstance();
                    addPath(object.getClass().getResource(object.getClass().getSimpleName() + ".class"));//baseFile.getPath().replace(".java", ".class"));
                    return object;
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                log.error(e.getMessage());
            }
        }
        return null;
    }

    private void addPath(File folder) {
        try {

            log.info("add to classpath: " + folder.getPath());
            URL url = folder.toURI().toURL();
            addPath(url);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected exception", e);
        }
    }

    private void addPath(URL url) {
        try {

            log.info("add to classpath: " + url.toString());

            URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{url});
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(classLoader, url);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected exception", e);
        }
    }
}
