package com.zomu.t.epion.tropic.test.tool.core.exception;

/**
 * Flowが見つからない例外.
 *
 * @author takashno
 */
public class FlowNotFoundException extends RuntimeException {

    public FlowNotFoundException(String type) {
        super("not found flow: '" + type + "'");
    }
}
