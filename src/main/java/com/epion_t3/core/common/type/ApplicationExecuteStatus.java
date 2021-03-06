/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.common.type;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * アプリケーション実行ステータス.
 *
 * @author takashno
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ApplicationExecuteStatus {

    WAIT("table-secondary", ExitCode.UNASSIGNED), SKIP("table-secondary", ExitCode.UNASSIGNED),
    RUNNING("table-info", ExitCode.UNASSIGNED), SUCCESS("table-success", ExitCode.NORMAL),
    ERROR("table-danger", ExitCode.ERROR), ASSERT_ERROR("table-danger", ExitCode.ASSERT_ERROR);

    private final String cssClass;

    private final ExitCode exitCode;

}
