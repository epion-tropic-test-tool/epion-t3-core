/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.command.handler.listener;

import com.epion_t3.core.command.runner.CommandRunner;
import com.epion_t3.core.common.bean.ExecuteCommand;
import com.epion_t3.core.common.bean.ExecuteFlow;
import com.epion_t3.core.common.bean.ExecuteScenario;
import com.epion_t3.core.common.context.ExecuteContext;

/**
 * コマンドの後処理リスナー.
 *
 * @author takashno
 */
public interface CommandAfterListener {

    /**
     * コマンド後処理.
     *
     * @param commandRunner
     * @param executeContext
     * @param executeScenario
     * @param executeFlow
     * @param executeCommand
     */
    void afterCommand(CommandRunner commandRunner, ExecuteContext executeContext, ExecuteScenario executeScenario,
            ExecuteFlow executeFlow, ExecuteCommand executeCommand);

}
