package com.epion_t3.core.scenario.bean;

import com.epion_t3.core.common.bean.ET3Notification;
import com.epion_t3.core.common.type.NotificationType;
import com.epion_t3.core.common.type.StageType;
import lombok.Builder;
import lombok.NonNull;

public class CommandValidateError extends ET3Notification {

    @NonNull
    private String commandId;

    @NonNull
    private String flowId;

    /**
     * コンストラクタ.
     */
    @Builder(builderMethodName = "commandValidateErrorBuilder")
    public CommandValidateError(StageType stage, NotificationType level, String message, Throwable error, String flowId, String commandId) {
        super(stage, level, message, error);
        this.flowId = flowId;
        this.commandId = commandId;
    }
}
