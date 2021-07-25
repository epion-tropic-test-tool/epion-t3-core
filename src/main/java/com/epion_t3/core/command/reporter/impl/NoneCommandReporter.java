/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.command.reporter.impl;

import com.epion_t3.core.command.bean.CommandResult;
import com.epion_t3.core.command.bean.NoneCommand;
import com.epion_t3.core.common.bean.ExecuteCommand;
import com.epion_t3.core.common.bean.ExecuteFlow;
import com.epion_t3.core.common.bean.ExecuteScenario;
import com.epion_t3.core.common.context.ExecuteContext;

import java.util.Map;

/**
 * レポーターが存在しないことを表すためのコマンドレポーター.
 */
public class NoneCommandReporter extends AbstractThymeleafCommandReporter<NoneCommand, CommandResult> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String templatePath() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setVariables(Map<String, Object> variable, NoneCommand command, CommandResult commandResult,
            ExecuteContext executeContext, ExecuteScenario executeScenario, ExecuteFlow executeFlow,
            ExecuteCommand executeCommand) {
    }
}
