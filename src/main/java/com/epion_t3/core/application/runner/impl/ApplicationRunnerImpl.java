/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.application.runner.impl;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.epion_t3.core.application.reporter.impl.ApplicationReporterImpl;
import com.epion_t3.core.application.runner.ApplicationRunner;
import com.epion_t3.core.common.annotation.ApplicationVersion;
import com.epion_t3.core.common.bean.ExecuteScenario;
import com.epion_t3.core.common.bean.config.ET3Config;
import com.epion_t3.core.common.context.Context;
import com.epion_t3.core.common.context.ExecuteContext;
import com.epion_t3.core.common.type.ApplicationExecuteStatus;
import com.epion_t3.core.common.type.Args;
import com.epion_t3.core.common.type.ExitCode;
import com.epion_t3.core.common.type.PathResolveMode;
import com.epion_t3.core.common.type.ScenarioExecuteStatus;
import com.epion_t3.core.common.type.StageType;
import com.epion_t3.core.common.util.ExecutionFileUtils;
import com.epion_t3.core.custom.parser.impl.CustomParserImpl;
import com.epion_t3.core.exception.SystemException;
import com.epion_t3.core.exception.handler.impl.ExceptionHandlerImpl;
import com.epion_t3.core.message.MessageManager;
import com.epion_t3.core.message.impl.CoreMessages;
import com.epion_t3.core.scenario.parser.impl.ScenarioParserImpl;
import com.epion_t3.core.scenario.runner.impl.ScenarioRunnerImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
        Arrays.stream(Args.values()).forEach(x -> {
            if (x.isRequired()) {
                OPTIONS.addRequiredOption(x.getShortName(), x.getLongName(), x.isHasArg(), x.getDescription());
            } else {
                OPTIONS.addOption(x.getShortName(), x.getLongName(), x.isHasArg(), x.getDescription());
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int execute(String[] args) {

        var parser = new DefaultParser();
        var cmd = (CommandLine) null;

        try {
            cmd = parser.parse(OPTIONS, args);
        } catch (ParseException e) {
            log.error("args error...", e);
            return ExitCode.ERROR.getExitCode();
        }

        // コンテキストの生成
        var context = new Context();

        // 実行コンテキストの生成
        var executeContext = new ExecuteContext();

        // 引数チェック結果（正常）
        var optionCheckSuccess = true;

        try {

            // 設定ファイルを読み込み＆設定
            // XXX: 設定ファイルと引数オプションでは引数オプションを優先設定とする
            setConfig(context, cmd);

            // 引数設定
            setOptions(context, cmd);

            // 引数チェック
            optionCheckSuccess = checkOptions(context);

            if (!optionCheckSuccess) {
                executeContext.setStatus(ApplicationExecuteStatus.ERROR);
                executeContext.setStage(StageType.ERROR_END);
                return ExitCode.ERROR.getExitCode();
            }

            // ロギング設定
            loggingSetting(context);

            // 結果ディレクトリの作成
            createResultDirectory(context, executeContext);

            // カスタム機能の解析（パース処理）
            CustomParserImpl.getInstance().parse(context, executeContext);

            // シナリオの解析（パース処理）
            ScenarioParserImpl.getInstance().parse(context, executeContext);

            // 実行
            new ScenarioRunnerImpl().execute(context, executeContext);

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

            // 例外ハンドリング
            ExceptionHandlerImpl.getInstance().handle(context, executeContext, t);

            executeContext.setStatus(ApplicationExecuteStatus.ERROR);

            executeContext.setStage(StageType.ERROR_END);

        } finally {

            executeContext.setEnd(LocalDateTime.now());

            // 所用時間を設定
            executeContext.setDuration(Duration.between(executeContext.getStart(), executeContext.getEnd()));

            // レポート出力
            // 引数チェックが正常に通っていない場合はレポートは不要
            if (optionCheckSuccess && !cmd.hasOption(Args.NO_REPORT.getShortName())) {
                report(context, executeContext);
            }

        }

        // 終了ステージ
        executeContext.setStage(StageType.NORMAL_END);

        return executeContext.getExitCode().getExitCode();

    }

    /**
     * 設定ファイルを読み込み、Optionを設定します.
     *
     * @param context コンテキスト
     * @param commandLine コマンドライン
     * @since 0.0.5
     */
    private void setConfig(final Context context, final CommandLine commandLine) {

        // 設定ファイルの取得
        if (commandLine.hasOption(Args.CONFIG.getShortName())) {
            var configPath = Paths.get(commandLine.getOptionValue(Args.CONFIG.getShortName()));
            if (!Files.exists(configPath)) {
                throw new SystemException(CoreMessages.CORE_ERR_0070, configPath);
            }
            try {
                var et3Config = context.getObjectMapper().readValue(configPath.toFile(), ET3Config.class);
                if (StringUtils.isNotEmpty(et3Config.getMode())) {
                    context.getOption().setMode(et3Config.getMode());
                }
                if (StringUtils.isNotEmpty(et3Config.getScenarioRootPath())) {
                    context.getOption().setRootPath(et3Config.getScenarioRootPath());
                }
                if (StringUtils.isNotEmpty(et3Config.getResultRootPath())) {
                    context.getOption().setResultRootPath(et3Config.getResultRootPath());
                }
                if (StringUtils.isNotEmpty(et3Config.getProfile())) {
                    context.getOption().setProfile(et3Config.getProfile());
                }
                context.getOption().setDebug(et3Config.isDebug());
                context.getOption().setNoReport(et3Config.isNoReport());
                context.getOption().setConsoleReport(et3Config.isConsoleReport());
                if (StringUtils.isNotEmpty(et3Config.getWebAssetPath())) {
                    context.getOption().setWebAssetPath(et3Config.getWebAssetPath());
                }
                if (StringUtils.isNotEmpty(et3Config.getPathResolveMode())) {
                    var pathResolveMode = PathResolveMode.valueOf(et3Config.getPathResolveMode());
                    if (pathResolveMode == null) {
                        throw new SystemException(CoreMessages.CORE_ERR_0072, configPath,
                                et3Config.getPathResolveMode());
                    }
                    context.getOption().setPathResolveMode(pathResolveMode);
                }
            } catch (IOException e) {
                throw new SystemException(e, CoreMessages.CORE_ERR_0071, configPath);
            }
        }
    }

    /**
     * 実行引数オプションをコンテキストへ設定する.
     *
     * @param context コンテキスト
     * @param commandLine コマンドライン
     */
    private void setOptions(final Context context, final CommandLine commandLine) {

        // 対象シナリオ
        if (commandLine.hasOption(Args.SCENARIO.getShortName())) {
            context.getOption().setTarget(commandLine.getOptionValue(Args.SCENARIO.getShortName()));
        }

        // シナリオルートパス
        if (commandLine.hasOption(Args.ROOT_PATH.getShortName())) {
            context.getOption().setRootPath(commandLine.getOptionValue(Args.ROOT_PATH.getShortName()));
        }

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
        context.getOption().setNoReport(commandLine.hasOption(Args.NO_REPORT.getShortName()));

        // コンソールレポート出力の設定
        context.getOption().setConsoleReport(commandLine.hasOption(Args.CONSOLE_REPORT.getShortName()));

        // WEBアセット基底パス
        if (commandLine.hasOption(Args.WEB_ASSET_PATH.getShortName())) {
            context.getOption().setWebAssetPath(commandLine.getOptionValue(Args.WEB_ASSET_PATH.getShortName()));
        }

        // デバッグの設定
        context.getOption().setDebug(commandLine.hasOption(Args.DEBUG.getShortName()));
    }

    /**
     * オプションのチェックを行う.
     * 
     * @param context コンテキスト
     * @return チェック結果（true: 正常、false: 異常）
     * @since 0.0.5
     */
    private boolean checkOptions(final Context context) {

        boolean result = true;

        // シナリオ配置ディレクトリのみここでチェック
        if (StringUtils.isEmpty(context.getOption().getRootPath())) {
            log.error(MessageManager.getInstance().getMessage(CoreMessages.CORE_ERR_0074));
            result = false;
        }

        return result;
    }

    /**
     * ロギングの設定を行う.
     *
     * @param context コンテキスト
     */
    private void loggingSetting(final Context context) {
        if (context.getOption().getDebug()) {
            System.setProperty("loggerLevel", "DEBUG");
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            loggerContext.getLoggerList().stream().forEach(x -> {
                if (!x.getName().startsWith("org.thymeleaf") && x.getLevel() != null) {
                    x.setLevel(Level.DEBUG);
                }
            });
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
    private void report(final Context context, final ExecuteContext executeContext) {
        // レポーターに処理を移譲
        ApplicationReporterImpl.getInstance().report(context, executeContext);

    }
}
