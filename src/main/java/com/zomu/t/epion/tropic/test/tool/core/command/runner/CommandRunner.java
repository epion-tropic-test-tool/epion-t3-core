package com.zomu.t.epion.tropic.test.tool.core.command.runner;

import com.zomu.t.epion.tropic.test.tool.core.command.model.CommandResult;
import com.zomu.t.epion.tropic.test.tool.core.context.execute.ExecuteCommand;
import com.zomu.t.epion.tropic.test.tool.core.context.execute.ExecuteContext;
import com.zomu.t.epion.tropic.test.tool.core.context.execute.ExecuteFlow;
import com.zomu.t.epion.tropic.test.tool.core.context.execute.ExecuteScenario;
import com.zomu.t.epion.tropic.test.tool.core.model.scenario.Command;
import org.slf4j.Logger;

import java.util.regex.Pattern;

/**
 * コマンドの実行処理インターフェース.
 *
 * @param <COMMAND>
 * @author takashno
 */
public interface CommandRunner<
        COMMAND extends Command,
        Context,
        EXECUTE_CONTEXT extends ExecuteContext,
        EXECUTE_SCENARIO extends ExecuteScenario,
        EXECUTE_FLOW extends ExecuteFlow,
        EXECUTE_COMMAND extends ExecuteCommand> {

    /**
     * 変数の抽出パターン.
     */
    Pattern EXTRACT_PATTERN = Pattern.compile("([^.]+)\\.(.+)");

    /**
     * コマンド実行処理.
     * 本メソッドは、カスタムコマンドにて実装する.
     *
     * @param command コマンド
     * @param logger  ロガー
     * @throws Exception 例外
     */
    CommandResult execute(final COMMAND command,
                          final Logger logger) throws Exception;


    /**
     * コマンド実行処理.
     *
     * @param command         実行するコマンド
     * @param context         コンテキスト
     * @param executeContext
     * @param executeScenario
     * @param executeFlow
     * @param executeCommand
     * @param logger
     * @throws Exception
     */
    void execute(final COMMAND command,
                 final Context context,
                 final EXECUTE_CONTEXT executeContext,
                 final EXECUTE_SCENARIO executeScenario,
                 final EXECUTE_FLOW executeFlow,
                 final EXECUTE_COMMAND executeCommand,
                 final Logger logger) throws Exception;

}
