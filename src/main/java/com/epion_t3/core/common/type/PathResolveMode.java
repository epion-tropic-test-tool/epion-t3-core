/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.common.type;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.annotation.Nullable;
import java.util.Arrays;

/**
 * パス解決モード.
 *
 * @since 0.0.5
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum PathResolveMode {

    /**
     * 実行中のシナリオディレクトリベースでの解決モード.
     */
    SCENARIO_DIR_BASE("SCENARIO_DIR_BASE"),

    /**
     * 属しているYAMLファイルが配置されているディレクトリベースでの解決モード.
     */
    BELONG_YAML_BASE("BELONG_YAML_BASE");

    /**
     * 値.
     */
    private String value;

    /**
     * 値からEnumを特定する.
     *
     * @param value 値
     * @return {@link PathResolveMode}
     */
    @Nullable
    public static PathResolveMode valueOfByValue(String value) {
        return Arrays.stream(values()).filter(x -> x.getValue().equals(value)).findFirst().orElse(null);
    }
}
