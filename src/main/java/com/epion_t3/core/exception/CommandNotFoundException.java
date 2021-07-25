/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.exception;

import lombok.Getter;

/**
 * コマンドが見つからない例外.
 *
 * @author takashno
 */
@Getter
public class CommandNotFoundException extends SystemException {

    /** コマンドID. */
    private String commandId;

    /**
     * コンストラクタ.
     * 
     * @param commandId コマンドID
     */
    public CommandNotFoundException(String commandId) {
        this.commandId = commandId;
    }
}
