package com.zomu.t.epion.tropic.test.tool.core.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 */
@Getter
@AllArgsConstructor
public enum ScenarioPaseErrorType {

    /**
     * 単純なYAML->Objectの変換時のエラー.
     */
    PARSE_ERROR,

    /**
     * コマンドが見つからない場合のエラー.
     */
    COMMAND_ERROR,

    /**
     * BeanValidationによるチェックエラー.
     */
    VALIDATION_ERROR


}
