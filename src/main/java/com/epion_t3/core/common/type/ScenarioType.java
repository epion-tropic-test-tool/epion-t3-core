/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.common.type;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author takashno
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ScenarioType {

    SCENARIO("com/epion_t3/core/common/bean/scenario"),

    PARTS("parts"),

    CONFIG("config");

    private final String value;

    public static ScenarioType valueOfByValue(final String value) {
        return Arrays.stream(values()).filter(x -> x.value.equals(value)).findFirst().orElse(null);
    }
}
