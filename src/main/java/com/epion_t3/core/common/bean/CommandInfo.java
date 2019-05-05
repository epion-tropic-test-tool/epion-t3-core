package com.epion_t3.core.common.bean;

import com.epion_t3.core.command.reporter.CommandReporter;
import com.epion_t3.core.command.runner.CommandRunner;
import com.epion_t3.core.common.bean.scenario.Command;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.Serializable;

/**
 * @auther takashno
 */
@Getter
@Setter
@Builder
public class CommandInfo implements Serializable {

    /**
     * デフォルトシリアルバージョンUID.
     */
    private static final long serialVersionUID = 1L;

    @NonNull
    private String id;

    private String summary;

    private String description;

    /**
     * コマンドモデル.
     */
    @NonNull
    private Class model;

    /**
     * アサートコマンドかどうか.
     */
    private Boolean assertCommand;

    /**
     * コマンド実行処理クラス.
     */
    @NonNull
    private Class<? extends CommandRunner> runner;

    /**
     * コマンド実行結果レポート出力クラス.
     */
    @NonNull
    private Class<? extends CommandReporter> reporter;

}
