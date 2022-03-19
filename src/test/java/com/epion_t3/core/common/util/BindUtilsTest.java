/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.common.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link BindUtils} のテストケース.
 */
@DisplayName("BindUtils のテストケース")
public class BindUtilsTest {

    /** 対象文字列 */
    static final String target = "abc${hoge}def${fuga}ghi${piyo}jkl${global.hoge}mno${scenario.hoge}pqr${flow.hoge}${global.fuga}${scenario.fuga}${flow.fuga}";

    /** Profile */
    static Map<String, String> profile;

    /** Global Variables */
    static Map<String, Object> globalVariables;

    /** Scenario Variables */
    static Map<String, Object> scenarioVariables;

    /** Flow Variables */
    static Map<String, Object> flowVariables;

    /**
     * 前処理. 毎回初期化する.
     */
    @BeforeEach
    void beforeAll() {
        profile = new HashMap<>();
        profile.put("hoge", "ほげ");
        profile.put("fuga", "ふが");
        profile.put("piyo", "ぴよ");

        globalVariables = new HashMap<>();
        globalVariables.put("hoge", "ほげ");
        globalVariables.put("fuga", "ふが");
        globalVariables.put("piyo", "ぴよ");

        scenarioVariables = new HashMap<>();
        scenarioVariables.put("hoge", "ほげ");
        scenarioVariables.put("fuga", "ふが");
        scenarioVariables.put("piyo", "ぴよ");

        flowVariables = new HashMap<>();
        flowVariables.put("hoge", "ほげ");
        flowVariables.put("fuga", "ふが");
        flowVariables.put("piyo", "ぴよ");
    }

    @Test
    @DisplayName("バインドで全て成功")
    void bind_001() {

        // 実行
        var result = BindUtils.getInstance().bind(target, profile, globalVariables, scenarioVariables, flowVariables);

        // アサーション
        assertThat(result).isEqualTo("abcほげdefふがghiぴよjklほげmnoほげpqrほげふがふがふが");
    }

    @Test
    @DisplayName("バインドで全て失敗")
    void bind_002() {

        profile.clear();
        globalVariables.clear();
        scenarioVariables.clear();
        flowVariables.clear();

        // 実行
        var result = BindUtils.getInstance().bind(target, profile, globalVariables, scenarioVariables, flowVariables);

        // アサーション
        assertThat(result).isEqualTo(target);
    }

    @Test
    @DisplayName("多重バインドで全て成功")
    void bind_003() {

        globalVariables.put("fuga", "000${global.piyo}000");
        scenarioVariables.put("fuga", "111${scenario.piyo}111");
        flowVariables.put("fuga", "222${flow.piyo}222");

        // 実行
        var result = BindUtils.getInstance().bind(target, profile, globalVariables, scenarioVariables, flowVariables);

        // アサーション
        assertThat(result).isEqualTo("abcほげdefふがghiぴよjklほげmnoほげpqrほげ000ぴよ000111ぴよ111222ぴよ222");
    }

    @Test
    @DisplayName("Profileのみバインドで全て成功")
    void bind_profile_only_001() {

        globalVariables.clear();
        scenarioVariables.clear();
        flowVariables.clear();

        // 実行
        var result = BindUtils.getInstance().bind(target, profile, globalVariables, scenarioVariables, flowVariables);

        // アサーション
        assertThat(result).isEqualTo(
                "abcほげdefふがghiぴよjkl${global.hoge}mno${scenario.hoge}pqr${flow.hoge}${global.fuga}${scenario.fuga}${flow.fuga}");
    }

    @Test
    @DisplayName("Profileのみバインドで一部失敗")
    void bind_profile_only_002() {

        profile.remove("fuga");
        globalVariables.clear();
        scenarioVariables.clear();
        flowVariables.clear();

        // 実行
        var result = BindUtils.getInstance().bind(target, profile, globalVariables, scenarioVariables, flowVariables);

        // アサーション
        assertThat(result).isEqualTo(
                "abcほげdef${fuga}ghiぴよjkl${global.hoge}mno${scenario.hoge}pqr${flow.hoge}${global.fuga}${scenario.fuga}${flow.fuga}");
    }

    @Test
    @DisplayName("Globalスコープのみバインドで全て成功")
    void bind_global_only_001() {

        profile.clear();
        scenarioVariables.clear();
        flowVariables.clear();

        // 実行
        var result = BindUtils.getInstance().bind(target, profile, globalVariables, scenarioVariables, flowVariables);

        // アサーション
        assertThat(result).isEqualTo(
                "abc${hoge}def${fuga}ghi${piyo}jklほげmno${scenario.hoge}pqr${flow.hoge}ふが${scenario.fuga}${flow.fuga}");
    }

    @Test
    @DisplayName("Globalスコープのみバインドで一部失敗")
    void bind_global_only_002() {

        profile.clear();
        globalVariables.remove("fuga");
        scenarioVariables.clear();
        flowVariables.clear();

        // 実行
        var result = BindUtils.getInstance().bind(target, profile, globalVariables, scenarioVariables, flowVariables);

        // アサーション
        assertThat(result).isEqualTo(
                "abc${hoge}def${fuga}ghi${piyo}jklほげmno${scenario.hoge}pqr${flow.hoge}${global.fuga}${scenario.fuga}${flow.fuga}");
    }

    @Test
    @DisplayName("Scenarioスコープのみバインドで全て成功")
    void bind_scenario_only_001() {

        profile.clear();
        globalVariables.clear();
        flowVariables.clear();

        // 実行
        var result = BindUtils.getInstance().bind(target, profile, globalVariables, scenarioVariables, flowVariables);

        // アサーション
        assertThat(result).isEqualTo(
                "abc${hoge}def${fuga}ghi${piyo}jkl${global.hoge}mnoほげpqr${flow.hoge}${global.fuga}ふが${flow.fuga}");
    }

    @Test
    @DisplayName("Scenarioスコープのみバインドで一部失敗")
    void bind_scenario_only_002() {

        profile.clear();
        globalVariables.clear();
        scenarioVariables.remove("fuga");
        flowVariables.clear();

        // 実行
        var result = BindUtils.getInstance().bind(target, profile, globalVariables, scenarioVariables, flowVariables);

        // アサーション
        assertThat(result).isEqualTo(
                "abc${hoge}def${fuga}ghi${piyo}jkl${global.hoge}mnoほげpqr${flow.hoge}${global.fuga}${scenario.fuga}${flow.fuga}");
    }

    @Test
    @DisplayName("Flowスコープのみバインドで全て成功")
    void bind_flow_only_001() {

        profile.clear();
        globalVariables.clear();
        scenarioVariables.clear();

        // 実行
        var result = BindUtils.getInstance().bind(target, profile, globalVariables, scenarioVariables, flowVariables);

        // アサーション
        assertThat(result).isEqualTo(
                "abc${hoge}def${fuga}ghi${piyo}jkl${global.hoge}mno${scenario.hoge}pqrほげ${global.fuga}${scenario.fuga}ふが");
    }

    @Test
    @DisplayName("Flowスコープのみバインドで一部失敗")
    void bind_flow_only_002() {

        profile.clear();
        globalVariables.clear();
        scenarioVariables.clear();
        flowVariables.remove("fuga");

        // 実行
        var result = BindUtils.getInstance().bind(target, profile, globalVariables, scenarioVariables, flowVariables);

        // アサーション
        assertThat(result).isEqualTo(
                "abc${hoge}def${fuga}ghi${piyo}jkl${global.hoge}mno${scenario.hoge}pqrほげ${global.fuga}${scenario.fuga}${flow.fuga}");
    }

}
