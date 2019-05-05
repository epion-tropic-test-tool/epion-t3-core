package com.epion_t3.core.scenario.bean;

import com.epion_t3.core.common.bean.ET3Notification;
import com.epion_t3.core.common.type.NotificationType;
import com.epion_t3.core.common.type.StageType;
import lombok.Builder;
import lombok.Getter;

/**
 * シナリオ解析エラー.
 *
 * @author takashno
 */
@Getter
public class ScenarioParseError extends ET3Notification {

    private final String filePath;

    /**
     * コンストラクタ.
     */
    @Builder(builderMethodName = "scenarioParseErrorBuilder")
    public ScenarioParseError(StageType stage, NotificationType level, String message, Throwable error, String filePath) {
        super(stage, level, message, error);
        this.filePath = filePath;
    }

}
