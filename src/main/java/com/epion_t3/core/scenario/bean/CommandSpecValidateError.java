package com.epion_t3.core.scenario.bean;

import com.epion_t3.core.common.bean.ET3Notification;
import com.epion_t3.core.common.type.NotificationType;
import com.epion_t3.core.common.type.StageType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CommandSpecValidateError extends ET3Notification {

    private final String customName;

    /**
     * コンストラクタ.
     */
    @Builder(builderMethodName = "csommandSpecValidateErrorBuilder")
    public CommandSpecValidateError(StageType stage, NotificationType level, String message, Throwable error, String customName) {
        super(stage, level, message, error);
        this.customName = customName;
    }
}
