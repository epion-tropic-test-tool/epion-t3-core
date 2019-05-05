package com.epion_t3.core.exception;

import lombok.Getter;

/**
 * Flowが見つからない例外.
 *
 * @author takashno
 */
@Getter
public class FlowNotFoundException extends SystemException {

    private String flowId;

    public FlowNotFoundException(String flowId) {
        super("not found flow: '" + flowId + "'");
    }
}
