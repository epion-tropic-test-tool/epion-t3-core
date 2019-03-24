package com.zomu.t.epion.tropic.test.tool.core.exception;

import lombok.Getter;

import java.io.IOException;

@Getter
public class CommandCanNotResolveException extends IOException {

    private String commandId;

    public CommandCanNotResolveException(String commandId) {
        super("can't resolve command: '" + commandId + "'");
        this.commandId = commandId;
    }

}
