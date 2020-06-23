package com.unister.semweb.apiontology;

import org.apache.cxf.common.classloader.ClassLoaderUtils;
import org.apache.cxf.common.util.Compiler;
import org.apache.cxf.helpers.JavaUtils;
import org.openapitools.codegen.CodegenConstants;
import org.openapitools.codegen.DefaultGenerator;
import org.openapitools.codegen.config.CodegenConfigurator;
import org.openapitools.codegen.config.GlobalSettings;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.Collectors;

public class OpenApiServiceDiscovery {

    public void discover(String url) throws IOException {

        GlobalSettings.setProperty(CodegenConstants.API_DOCS, Boolean.FALSE.toString());
        GlobalSettings.setProperty(CodegenConstants.MODEL_DOCS, Boolean.FALSE.toString());
        GlobalSettings.setProperty(CodegenConstants.API_TESTS, Boolean.FALSE.toString());
        GlobalSettings.setProperty(CodegenConstants.MODEL_TESTS, Boolean.FALSE.toString());
        CodegenConfigurator configurator = new CodegenConfigurator();
        configurator.setApiPackage("org.example.api");
        configurator.setModelPackage("org.example.model");
        configurator.setGeneratorName("java");
        configurator.setInputSpec(url);
        configurator.setLibrary("resttemplate");
        configurator.addAdditionalProperty("hideGenerationTimestamp", true);
        Path tempDirectory = Files.createTempDirectory("tmp");
        configurator.setOutputDir(tempDirectory.toString());
        List<File> files = new DefaultGenerator().opts(configurator.toClientOptInput())
                                                 .generate();

        ProcessBuilder builder = new ProcessBuilder("chmod", "+x", "gradlew");
        builder.directory(tempDirectory.toFile());
        builder.inheritIO();
        builder.start();

        builder = new ProcessBuilder("./gradlew", "clean", "build");
        builder.directory(tempDirectory.toFile());
        builder.inheritIO();
        builder.start();

        File libs = tempDirectory.resolve("build")
                                 .resolve("libs")
                                 .toFile();
        URL[] jarToLoad = Arrays.stream(Objects.requireNonNull(libs.listFiles()))
                                .filter(f -> f.getName()
                                              .endsWith(".jar"))
                                .map(f -> {
                                    try {
                                        return f.toPath()
                                                .toUri()
                                                .toURL();
                                    }
                                    catch (MalformedURLException e) {
                                        throw new RuntimeException(e);
                                    }
                                })
                                .toArray(URL[]::new);

        final ClassLoader cl = ClassLoaderUtils.getURLClassLoader(jarToLoad, Thread.currentThread().getContextClassLoader());
        ClassLoaderUtils.setThreadContextClassloader(cl);

    }

    public static void main(String... args) throws IOException {
        new OpenApiServiceDiscovery().discover(
                "https://raw.githubusercontent.com/openapitools/openapi-generator/master/modules/openapi-generator/src/test/resources/2_0/petstore.yaml");
    }
}
