package com.epion_t3.core.common.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.annotation.Nullable;
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
    SCENARIO("com/epion_t3/core/common/bean/scenario"),

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
    @Nullable
    public static ReferenceVariableType valueOfByName(String name) {
        return Arrays.stream(values())
                .filter(x -> x.getName().equals(name))
                .findFirst().orElse(null);
    }
}
