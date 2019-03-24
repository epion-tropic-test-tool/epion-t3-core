package com.zomu.t.epion.tropic.test.tool.core.exception.bean;

import com.zomu.t.epion.tropic.test.tool.core.type.ScenarioPaseErrorType;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.Serializable;
import java.nio.file.Path;

/**
 * シナリオ静的エラー情報（解析・チェック）.
 */
@Getter
@Setter
@Builder
public class ScenarioParseError implements Serializable {

    /**
     * 対象エラーファイル.
     */
    @NonNull
    private Path filePath;

    /**
     * エラー種別.
     */
    @NonNull
    private ScenarioPaseErrorType type;

    /**
     * メッセージ.
     */
    @NonNull
    private String message;

    /**
     * 対象を詳細に指定したい場合の補足情報.
     */
    private String target;

    /**
     * 対象に設定されている値を指定したい場合の補足情報.
     */
    private Object value;


}
