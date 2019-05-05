package com.epion_t3.core.common.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author takashno
 */
@Getter
@AllArgsConstructor
public enum ScenarioType {

    SCENARIO("com/epion_t3/core/common/bean/scenario"),

    PARTS("parts"),

    CONFIG("config");

    private String value;

    public static ScenarioType valueOfByValue(final String value) {
        return Arrays.stream(values()).filter(x -> x.value.equals(value)).findFirst().orElse(null);
    }
}
