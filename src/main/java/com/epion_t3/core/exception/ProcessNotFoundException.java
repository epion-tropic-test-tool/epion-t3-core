package com.epion_t3.core.exception;

/**
 * プロセスが見つからない例外.
 *
 * @author takashno
 */
public class ProcessNotFoundException extends SystemException {

    private String messageCode;

    public ProcessNotFoundException(String processId) {
        super("'" + processId + "' is not found in scenario...");
    }
}
