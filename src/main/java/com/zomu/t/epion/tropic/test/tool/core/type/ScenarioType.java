package com.zomu.t.epion.tropic.test.tool.core.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author takashno
 */
@Getter
@AllArgsConstructor
public enum ScenarioType {

    SCENARIO("scenario"),

    PARTS("parts"),

    CONFIG("config");

    private String value;

    public static ScenarioType valueOfByValue(final String value) {
        return Arrays.stream(values()).filter(x -> x.value.equals(value)).findFirst().orElse(null);
    }
}
