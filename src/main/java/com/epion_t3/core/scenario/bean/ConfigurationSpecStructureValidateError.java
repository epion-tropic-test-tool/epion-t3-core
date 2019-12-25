/* Copyright (c) 2017-2019 Nozomu Takashima. */
package com.epion_t3.core.scenario.bean;

import com.epion_t3.core.common.type.NotificationType;
import com.epion_t3.core.common.type.StageType;
import lombok.Builder;
import lombok.Getter;

/**
 * カスタム設定定義の設計構成検証エラー.
 *
 * @author takashno
 */
@Getter
public class ConfigurationSpecStructureValidateError extends ConfigurationSpecValidateError {

    /**
     * 要素名
     */
    private final String structureName;

    /**
     * コンストラクタ.
     */
    @Builder(builderMethodName = "configurationSpecStructureValidateErrorBuilder")
    public ConfigurationSpecStructureValidateError(StageType stage, NotificationType level, String message,
            Throwable error, String customName, String structureName) {
        super(stage, level, message, error, customName);
        this.structureName = structureName;
    }
}
