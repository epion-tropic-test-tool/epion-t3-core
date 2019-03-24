package com.zomu.t.epion.tropic.test.tool.core.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 参照変数種別.
 *
 * @author takashno
 */
@Getter
@AllArgsConstructor
public enum ReferenceVariableType {

    /**
     * グローバルスコープ.
     */
    GLOBAL("global"),

    /**
     * シナリオスコープ.
     */
    SCENARIO("scenario"),

    /**
     * Flowスコープ.
     */
    FLOW("flow"),

    /**
     * 固定値.
     */
    FIX("fix");

    /**
     * 名称.
     */
    private String name;

    /**
     * 名称からEnumを特定する.
     *
     * @param name 名称
     * @return {@link ReferenceVariableType}
     */
    public static ReferenceVariableType valueOfByName(String name) {
        return Arrays.stream(values())
                .filter(x -> x.getName().equals(name))
                .findFirst().orElse(null);
    }
}
