package com.zomu.t.epion.tropic.test.tool.core.exception;

/**
 * プロセスが見つからない例外.
 *
 * @author takashno
 */
public class ProcessNotFoundException extends RuntimeException {

    private String messageCode;

    public ProcessNotFoundException(String processId) {
        super("'" + processId + "' is not found in scenario...");
    }
}
