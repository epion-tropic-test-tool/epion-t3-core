package com.zomu.t.epion.tropic.test.tool.core.exception;

public class ScenarioNotFoundException extends RuntimeException {

    public ScenarioNotFoundException(String scenarioId) {
        super("not found scenario: '" + scenarioId + "'");
    }
}
