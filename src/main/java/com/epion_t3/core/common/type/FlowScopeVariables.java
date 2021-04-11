/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.common.type;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Flowスコープの固定変数定義.
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum FlowScopeVariables {

    /**
     * 現在実行中のFlow.
     */
    CURRENT_FLOW("currentFlow"),

    /**
     * 現在実行中のFlowの実行ID.
     */
    CURRENT_FLOW_EXECUTE_ID("currentFlowExecuteId"),

    /**
     * 現在実行中のコマンド.
     */
    CURRENT_COMMAND("currentCommand"),

    /**
     * 現在実行中のコマンドの実行ID.
     */
    CURRENT_COMMAND_EXECUTE_ID("currentProcessExecuteId"),

    /**
     * 実行コマンドが格納されているパス文字列.
     */
    CURRENT_COMMAND_DIR("currentCommandDir");

    /**
     * 変数名.
     */
    private final String name;

}
