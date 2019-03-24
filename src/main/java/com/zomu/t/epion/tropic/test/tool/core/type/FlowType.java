package com.zomu.t.epion.tropic.test.tool.core.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * Flow種別
 */
@Getter
@AllArgsConstructor
public enum FlowType {

    PROCESS("process"),

    SCENARIO("include"),

    ITERATE("iterate"),

    BRANCH("branch");

    private String value;

    public static FlowType valueOfByValue(final String value) {
        return Arrays.stream(values()).filter(x -> x.value.equals(value)).findFirst().orElse(null);
    }
}

