package com.zomu.t.epion.tropic.test.tool.core.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExitCode {

    /**
     * 正常.
     */
    NORMAL(0),

    /**
     * シナリオ定義エラー.
     */
    SCENARIO_ERROR(1),

    /**
     * アサートエラー.
     */
    ASSERT_ERROR(2),

    /**
     * 実行時エラー.
     */
    ERROR(9),


    /**
     * 割り当てられていない.
     */
    UNASSIGNED(100);


    private int exitCode;
}
