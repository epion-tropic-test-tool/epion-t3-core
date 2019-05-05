package com.epion_t3.core.exception;

public class ScenarioNotFoundException extends SystemException {

    private String scenarioId;

    public ScenarioNotFoundException(String scenarioId) {
        this.scenarioId = scenarioId;
    }
}
