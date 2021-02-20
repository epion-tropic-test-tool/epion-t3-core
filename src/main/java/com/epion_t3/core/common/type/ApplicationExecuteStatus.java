/* Copyright (c) 2017-2020 Nozomu Takashima. */
package com.epion_t3.core.common.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * アプリケーション実行ステータス.
 *
 * @author takashno
 */
@Getter
@AllArgsConstructor
public enum ApplicationExecuteStatus {

    WAIT("table-secondary", ExitCode.UNASSIGNED), SKIP("table-secondary", ExitCode.UNASSIGNED),
    RUNNING("table-info", ExitCode.UNASSIGNED), SUCCESS("table-success", ExitCode.NORMAL),
    ERROR("table-danger", ExitCode.ERROR), ASSERT_ERROR("table-danger", ExitCode.ASSERT_ERROR);

    private String cssClass;

    private ExitCode exitCode;

}
