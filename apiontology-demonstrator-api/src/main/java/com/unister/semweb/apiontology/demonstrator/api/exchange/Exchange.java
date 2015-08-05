package com.unister.semweb.apiontology.demonstrator.api.exchange;

public class Exchange {

    private String action;
    private String uri;
    private ConfigurationObject configurations;
    private ExperimentInput experimentInput;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public ConfigurationObject getConfigurations() {
        return configurations;
    }

    public void setConfigurations(ConfigurationObject configurations) {
        this.configurations = configurations;
    }

    public ExperimentInput getExperimentInput() {
        return experimentInput;
    }

    public void setExperimentInput(ExperimentInput experimentInput) {
        this.experimentInput = experimentInput;
    }

}
