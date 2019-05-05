package com.epion_t3.core.command.handler.listener;

import com.epion_t3.core.command.runner.CommandRunner;
import com.epion_t3.core.common.bean.ExecuteCommand;
import com.epion_t3.core.common.context.ExecuteContext;
import com.epion_t3.core.common.bean.ExecuteFlow;
import com.epion_t3.core.common.bean.ExecuteScenario;

/**
 * コマンド前処理リスナーインターフェース.
 *
 * @author takashno
 */
public interface CommandBeforeListener {

    /**
     * コマンド前処理.
     *
     * @param commandRunner
     * @param executeContext
     * @param executeScenario
     * @param executeFlow
     * @param executeCommand
     */
    void beforeCommand(CommandRunner commandRunner,
                       ExecuteContext executeContext,
                       ExecuteScenario executeScenario,
                       ExecuteFlow executeFlow,
                       ExecuteCommand executeCommand);

}
