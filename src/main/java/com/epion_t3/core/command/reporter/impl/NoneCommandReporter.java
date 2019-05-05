package com.epion_t3.core.command.reporter.impl;

import com.epion_t3.core.command.bean.CommandResult;
import com.epion_t3.core.command.bean.NoneCommand;
import com.epion_t3.core.common.bean.ExecuteCommand;
import com.epion_t3.core.common.context.ExecuteContext;
import com.epion_t3.core.common.bean.ExecuteFlow;
import com.epion_t3.core.common.bean.ExecuteScenario;

import java.util.Map;

public class NoneCommandReporter
        extends AbstractThymeleafCommandReporter<NoneCommand, CommandResult> {
    @Override
    public String templatePath() {
        return null;
    }

    @Override
    public void setVariables(Map<String, Object> variable,
                             NoneCommand command,
                             CommandResult commandResult,
                             ExecuteContext executeContext,
                             ExecuteScenario executeScenario,
                             ExecuteFlow executeFlow,
                             ExecuteCommand executeCommand) {
    }
}
