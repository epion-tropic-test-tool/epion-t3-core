/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.common.type;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.annotation.Nullable;
import java.util.Arrays;

/**
 * Flow種別
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum FlowType {

    PROCESS("process"),

    SCENARIO("include"),

    ITERATE("iterate"),

    BRANCH("branch");

    private final String value;

    @Nullable
    public static FlowType valueOfByValue(final String value) {
        return Arrays.stream(values()).filter(x -> x.value.equals(value)).findFirst().orElse(null);
    }
}
