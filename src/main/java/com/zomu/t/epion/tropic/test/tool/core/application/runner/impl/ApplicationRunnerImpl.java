package com.zomu.t.epion.tropic.test.tool.core.application.runner.impl;

import com.zomu.t.epion.tropic.test.tool.core.annotation.ApplicationVersion;
import com.zomu.t.epion.tropic.test.tool.core.application.reporter.impl.ApplicationReporterImpl;
import com.zomu.t.epion.tropic.test.tool.core.application.runner.ApplicationRunner;
import com.zomu.t.epion.tropic.test.tool.core.context.Context;
import com.zomu.t.epion.tropic.test.tool.core.context.execute.ExecuteContext;
import com.zomu.t.epion.tropic.test.tool.core.context.execute.ExecuteScenario;
import com.zomu.t.epion.tropic.test.tool.core.exception.handler.BaseExceptionHandler;
import com.zomu.t.epion.tropic.test.tool.core.message.MessageManager;
import com.zomu.t.epion.tropic.test.tool.core.message.impl.CoreMessages;
import com.zomu.t.epion.tropic.test.tool.core.scenario.parser.impl.BaseScenarioParser;
import com.zomu.t.epion.tropic.test.tool.core.scenario.runner.ScenarioRunner;
import com.zomu.t.epion.tropic.test.tool.core.scenario.runner.impl.ScenarioRunnerImpl;
import com.zomu.t.epion.tropic.test.tool.core.type.ApplicationExecuteStatus;
import com.zomu.t.epion.tropic.test.tool.core.type.Args;
import com.zomu.t.epion.tropic.test.tool.core.type.ExitCode;
import com.zomu.t.epion.tropic.test.tool.core.type.ScenarioExecuteStatus;
import com.zomu.t.epion.tropic.test.tool.core.util.ExecutionFileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * アプリケーション実行処理.
 *
 * @author takashno
 */
@ApplicationVersion(version = "v1.0")
@Slf4j
public class ApplicationRunnerImpl implements ApplicationRunner<Context> {

    /**
     * CLIオプション.
     */
    private static final Options OPTIONS = new Options();

    static {

        // 引数定義をCLIオプション化する
        // Base(v1.0)については、coreをそのまま引き継ぐ
        Arrays.stream(Args.values()).forEach(
                x -> {
                    if (x.isRequired()) {
                        OPTIONS.addRequiredOption(x.getShortName(), x.getLongName(), x.isHasArg(), x.getDescription());
                    } else {
                        OPTIONS.addOption(x.getShortName(), x.getLongName(), x.isHasArg(), x.getDescription());
                    }
                }
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int execute(String[] args) {

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(OPTIONS, args);
        } catch (ParseException e) {
            log.error("args error...", e);
            return ExitCode.ERROR.getExitCode();
        }

        // コンテキストの生成
        Context context = new Context();

        // 実行コンテキストの生成
        ExecuteContext executeContext = new ExecuteContext();

        // エラー
        Throwable error = null;

        try {

            // 引数設定
            setOptions(context, cmd);

            // 結果ディレクトリの作成
            createResultDirectory(context, executeContext);

            // シナリオの解析（パース処理）
            BaseScenarioParser.getInstance().parse(context);

            // 実行
            ScenarioRunner scenarioRunner = new ScenarioRunnerImpl();
            scenarioRunner.execute(context, executeContext);

            // 終了判定
            executeContext.setStatus(ApplicationExecuteStatus.SUCCESS);
            // 全シナリオを走査
            for (ExecuteScenario executeScenario : executeContext.getScenarios()) {
                if (executeScenario.getStatus() == ScenarioExecuteStatus.ERROR) {
                    // エラーが１つでも存在すればエラーとする
                    executeContext.setStatus(ApplicationExecuteStatus.ERROR);
                    break;
                } else if (executeScenario.getStatus() == ScenarioExecuteStatus.ASSERT_ERROR) {
                    // アサートエラーが存在すればアサートエラーを設定
                    executeContext.setStatus(ApplicationExecuteStatus.ASSERT_ERROR);
                }
            }

            executeContext.setExitCode(executeContext.getStatus().getExitCode());

        } catch (Throwable t) {

            error = t;

            executeContext.setStatus(ApplicationExecuteStatus.ERROR);

            // 例外ハンドリング
            handleGlobalException(context, t);

        } finally {

            executeContext.setEnd(LocalDateTime.now());

            // 所用時間を設定
            executeContext.setDuration(
                    Duration.between(
                            executeContext.getStart(),
                            executeContext.getEnd()));

            // レポート出力
            if (!cmd.hasOption(Args.NOREPORT.getShortName())) {
                report(context, executeContext, error);
            }

        }

        return executeContext.getExitCode().getExitCode();

    }

    /**
     * @param context
     * @param t
     */
    @Override
    public void handleGlobalException(final Context context, final Throwable t) {

        BaseExceptionHandler.getInstance().handle(context, t);

    }

    /**
     * 実行引数オプションをコンテキストへ設定する.
     *
     * @param context     コンテキスト
     * @param commandLine
     */
    private void setOptions(final Context context, final CommandLine commandLine) {
        String version = commandLine.getOptionValue(Args.VERSION.getShortName());
        String rootPath = commandLine.getOptionValue(Args.ROOT_PATH.getShortName());
        String target = commandLine.getOptionValue(Args.SCENARIO.getShortName());

        // 必須パラメータの取得
        context.getOption().setVersion(version);
        context.getOption().setRootPath(rootPath);
        context.getOption().setTarget(target);

        // プロファイルの取得
        if (commandLine.hasOption(Args.PROFILE.getShortName())) {
            context.getOption().setProfile(commandLine.getOptionValue(Args.PROFILE.getShortName()));
        }

        // 実行結果出力ディレクトリの取得
        if (commandLine.hasOption(Args.RESULT_ROOT_PATH.getShortName())) {
            context.getOption().setResultRootPath(commandLine.getOptionValue(Args.RESULT_ROOT_PATH.getShortName()));
        }

        // モードの取得
        if (commandLine.hasOption(Args.MODE.getShortName())) {
            context.getOption().setMode(commandLine.getOptionValue(Args.MODE.getShortName()));
        }

        // レポート出力無の設定
        if (commandLine.hasOption(Args.NOREPORT.getShortName())) {
            context.getOption().setNoreport(true);
        }

        // デバッグの設定
        if (commandLine.hasOption(Args.DEBUG.getShortName())) {
            context.getOption().setDebug(true);
        }
    }

    /**
     * 結果ディレクトリが未作成であった場合に、作成します.
     *
     * @param context コンテキスト
     */
    private void createResultDirectory(final Context context, final ExecuteContext executeContext) {
        ExecutionFileUtils.createResultDirectory(context, executeContext);
    }

    /**
     * レポート出力を行う.
     *
     * @param context コンテキスト
     */
    private void report(final Context context,
                        final ExecuteContext executeContext,
                        final Throwable error) {

        // レポーターに処理を移譲
        ApplicationReporterImpl.getInstance().report(context, executeContext, error);

    }
}
