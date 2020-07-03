package com.example.cffg.processor.cucumber.glue;

import io.cucumber.core.backend.ObjectFactory;
import io.cucumber.core.feature.Options;
import io.cucumber.core.snippets.SnippetType;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class CustomRuntimeOptions implements Options, io.cucumber.core.runner.Options, io.cucumber.core.plugin.Options, io.cucumber.core.filter.Options, io.cucumber.core.backend.Options {

    private final List<URI> glue = new ArrayList();
    private final List<URI> featurePaths = new ArrayList();

    public CustomRuntimeOptions(URI featurePaths, URI gluePaths) {
        this.glue.add(gluePaths);
        this.featurePaths.add(featurePaths);
    }

    @Override
    public List<URI> getFeaturePaths() {
        return featurePaths;
    }

    @Override
    public List<String> getTagExpressions() {
        return null;
    }

    @Override
    public List<Pattern> getNameFilters() {
        return null;
    }

    @Override
    public Map<URI, Set<Integer>> getLineFilters() {
        return null;
    }

    @Override
    public int getLimitCount() {
        return 0;
    }

    @Override
    public Iterable<Plugin> plugins() {
        return null;
    }

    @Override
    public boolean isMonochrome() {
        return false;
    }

    @Override
    public boolean isStrict() {
        return false;
    }

    @Override
    public List<URI> getGlue() {
        return glue;
    }

    @Override
    public boolean isDryRun() {
        return false;
    }

    @Override
    public SnippetType getSnippetType() {
        return SnippetType.UNDERSCORE;
    }

    @Override
    public Class<? extends ObjectFactory> getObjectFactoryClass() {
        return null;
    }
}
