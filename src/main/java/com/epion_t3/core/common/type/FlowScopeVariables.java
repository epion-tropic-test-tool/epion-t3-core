/* Copyright (c) 2017-2019 Nozomu Takashima. */
package com.epion_t3.core.common.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FlowScopeVariables {

    CURRENT_FLOW("currentFlow"),

    CURRENT_FLOW_EXECUTE_ID("currentFlowExecuteId"),

    CURRENT_COMMAND("currentCommand"),

    CURRENT_COMMAND_EXECUTE_ID("currentProcessExecuteId"),

    /** 実行コマンドが格納されているパス文字列. */
    CURRENT_COMMAND_DIR("currentCommandDir"),

    /** 実行コマンドが格納されているパスオブジェクト（java.nio.file.Path）. */
    CURRENT_COMMAND_DIR_PATH("currentCommandDirPath");

    private String name;

}
