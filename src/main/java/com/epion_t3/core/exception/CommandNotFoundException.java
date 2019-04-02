package com.epion_t3.core.exception;

import lombok.Getter;

/**
 * コマンドが見つからない例外.
 *
 * @author takashno
 */
@Getter
public class CommandNotFoundException extends RuntimeException {

    private String commandId;

    public CommandNotFoundException(String commandId) {
        super("not found command: '" + commandId + "'");
        this.commandId = commandId;
    }
}
