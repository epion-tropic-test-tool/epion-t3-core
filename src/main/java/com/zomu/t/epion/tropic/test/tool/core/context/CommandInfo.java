package com.zomu.t.epion.tropic.test.tool.core.context;

import com.zomu.t.epion.tropic.test.tool.core.command.reporter.CommandReporter;
import com.zomu.t.epion.tropic.test.tool.core.command.runner.CommandRunner;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class CommandInfo implements Serializable {

    @NonNull
    private String id;

    private String summary;

    private String description;

    /**
     * コマンドモデル.
     */
    @NonNull
    private Class<?> model;

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
