package com.epion_t3.core.scenario.bean;

import com.epion_t3.core.common.bean.ET3Notification;
import com.epion_t3.core.common.type.NotificationType;
import com.epion_t3.core.common.type.StageType;
import lombok.Builder;
import lombok.Getter;

/**
 * カスタム設定定義の設計検証エラー.
 *
 * @author takashno
 */
@Getter
public class ConfigurationSpecValidateError extends ET3Notification {

    private final String customName;

    /**
     * コンストラクタ.
     */
    @Builder(builderMethodName = "configurationSpecValidateErrorBuilder")
    public ConfigurationSpecValidateError(StageType stage, NotificationType level, String message, Throwable error, String customName) {
        super(stage, level, message, error);
        this.customName = customName;
    }
}
