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
import java.util.Collections;

@Slf4j
class JavaFileCompiler {

    private Config config;

    JavaFileCompiler(Config config) {
        this.config = config;
    }

    Class<?> compile(Pair<String, File> pair) {
        File baseFile = pair.getRight();
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        Iterable<? extends JavaFileObject> compileUnit = fileManager.getJavaFileObjectsFromFiles(Collections.singleton(baseFile));
        String[] options = {"-d", "cucumber/runtime"};
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, null, null, compileUnit);

        boolean success = task.call();

        log.info("Success: " + success);

        if (success) {
            addPath(baseFile.getPath().replace(".java", ".class"));
            String className = baseFile.getPath().replace(".java", "").replace("\\", ".");
            File root = new File(config.getProperty("DEFAULT_TEMP_PATH") + "/src/test/java/");
            try {
                log.info(className);
                URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{root.toURI().toURL()});
                Class<?> aClass = Class.forName(pair.getKey(), true, classLoader);
                try {
                    Object object = aClass.newInstance();
                    Class<?> objectClass = object.getClass();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                Method[] methods = aClass.getMethods();

                Class<?> cls = aClass;

                return cls;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                log.error(e.getMessage());
            }
        }
        return null;
    }

    private void addPath(String basePath) {
        try {

            log.info(basePath);
            File folder = new File(basePath);
            URL url = folder.toURI().toURL();

            URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{folder.toURI().toURL()});
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(classLoader, url);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected exception", e);
        }
    }
}
