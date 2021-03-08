/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.scenario.runner.impl;

import com.epion_t3.core.common.bean.ET3Notification;
import com.epion_t3.core.common.bean.ExecuteScenario;
import com.epion_t3.core.common.bean.Option;
import com.epion_t3.core.common.bean.scenario.ET3Base;
import com.epion_t3.core.common.bean.scenario.Flow;
import com.epion_t3.core.common.bean.scenario.Scenario;
import com.epion_t3.core.common.context.Context;
import com.epion_t3.core.common.context.ExecuteContext;
import com.epion_t3.core.common.type.NotificationType;
import com.epion_t3.core.common.type.ScenarioExecuteStatus;
import com.epion_t3.core.common.type.ScenarioScopeVariables;
import com.epion_t3.core.common.type.StageType;
import com.epion_t3.core.common.util.BindUtils;
import com.epion_t3.core.common.util.ExecutionFileUtils;
import com.epion_t3.core.exception.ScenarioNotFoundException;
import com.epion_t3.core.exception.SystemException;
import com.epion_t3.core.flow.bean.FlowResult;
import com.epion_t3.core.flow.resolver.impl.FlowRunnerResolverImpl;
import com.epion_t3.core.message.MessageManager;
import com.epion_t3.core.message.impl.CoreMessages;
import com.epion_t3.core.scenario.reporter.impl.ScenarioReporterImpl;
import com.epion_t3.core.scenario.runner.ScenarioRunner;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * シナリオ実行処理.
 *
 * @author takashno
 */
@Slf4j
public class ScenarioRunnerImpl implements ScenarioRunner<Context, ExecuteContext> {

    /**
     * {@inheritDoc}
     *
     * @param context コンテキスト
     */
    @Override
    public void execute(final Context context, final ExecuteContext executeContext) {

        // シナリオ構築ステージ
        executeContext.setStage(StageType.BUILD_SCENARIO);

        // 実行シナリオの選択
        var et3 = context.getOriginal().getOriginals().get(context.getOption().getTarget());

        if (et3 == null) {
            executeContext.addNotification(ET3Notification.builder()
                    .stage(executeContext.getStage())
                    .level(NotificationType.ERROR)
                    .message(MessageManager.getInstance()
                            .getMessage(CoreMessages.CORE_ERR_0045, context.getOption().getTarget()))
                    .build());
            throw new ScenarioNotFoundException(context.getOption().getTarget());
        }

        if (et3.getScenarios().isEmpty()) {
            // 単シナリオ起動
            var scenarioRef = new Scenario();
            scenarioRef.setRef(context.getOption().getTarget());
            scenarioRef.setProfile(context.getOption().getProfile());
            scenarioRef.setMode(context.getOption().getMode());
            scenarioRef.setNoreport(context.getOption().getNoreport());
            scenarioRef.setMode(context.getOption().getMode());
            executeScenario(context, executeContext, scenarioRef);
        } else {
            // 複数シナリオ起動
            for (var scenarioRef : et3.getScenarios()) {
                executeScenario(context, executeContext, scenarioRef);
            }
        }

    }

    /**
     * シナリオ実行.
     *
     * @param context コンテキスト
     * @param executeContext 実行コンテキスト
     * @param scenarioRef シナリオ参照
     */
    private void executeScenario(Context context, ExecuteContext executeContext, Scenario scenarioRef) {

        // シナリオ実行ステージ
        executeContext.setStage(StageType.RUN_SCENARIO);

        // グローバル変数の設定
        // シナリオの実行毎にシナリオ変数に設定されたグローバル変数は元に戻す
        reSettingGlobalVariables(context, executeContext);

        var et3 = context.getOriginal().getOriginals().get(scenarioRef.getRef());

        if (et3 == null) {
            throw new ScenarioNotFoundException(scenarioRef.getRef());
        }

        // オプション解決
        Option option = SerializationUtils.clone(context.getOption());
        // プロファイルのオーバーライド
        if (StringUtils.isNotEmpty(scenarioRef.getProfile()) && StringUtils.isNotEmpty(option.getProfile())
                && !option.getProfile().contains(scenarioRef.getProfile())) {
            option.setProfile(option.getProfile() + "," + scenarioRef.getProfile());
        }
        // モードのオーバーライド
        if (StringUtils.isNotEmpty(scenarioRef.getMode())) {
            option.setMode(scenarioRef.getMode());
        }
        // レポート出力有無のオーバーライド
        if (scenarioRef.getNoreport() != null) {
            option.setNoreport(scenarioRef.getNoreport());
        }
        // デバッグ指定のオーバーライド
        if (scenarioRef.getDebug() != null) {
            option.setDebug(scenarioRef.getDebug());
        }

        var executeScenario = new ExecuteScenario();
        executeScenario.setOption(option);
        executeScenario.setInfo(et3.getInfo());
        executeScenario.setFqsn(et3.getInfo().getId());
        executeContext.getScenarios().add(executeScenario);

        // エラー
        Throwable error = null;

        try {

            // シナリオ実行開始時間を設定
            executeScenario.setStart(LocalDateTime.now());

            // シナリオ開始ログ出力
            outputStartScenarioLog(context, executeScenario);

            // プロファイルの解決
            setProfiles(context, executeScenario);

            // 結果ディレクトリの作成
            ExecutionFileUtils.createScenarioResultDirectory(context, executeContext, executeScenario);

            // シナリオスコープの変数を設定
            settingScenarioVariables(context, executeScenario, et3);

            var flowResult = (FlowResult) null;

            // 現状マルチスレッドで動かす想定がない（AtomicBooleanは使用しない）
            var exitFlg = false;

            // 全てのフローを実行
            for (Flow flow : et3.getFlows()) {

                if (flowResult != null) {
                    // 前Flowの結果によって処理を振り分ける
                    switch (flowResult.getStatus()) {
                    case ERROR:
                        // Flow自体の実行時にエラーが発生しているため終了する.
                        log.debug("flow execute error occurred.");
                        exitFlg = true;
                        break;
                    case FORCE_EXIT:
                        // 即時終了
                        log.debug("force exit scenario.");
                        exitFlg = true;
                        break;
                    case BREAK:
                    case CONTINUE:
                        // ループ系Flow用ステータスのため判断不要.
                        // Flowはネスト構造（子Flow）を保持できるが、シナリオ実行処理から呼び出されるFlowからは、
                        // このような制御ステータスを返却しない.
                        // 故障の可能性、もしくはFlowの組み方が悪い
                        log.debug("invalid flow status in ScenarioRunnerImpls. status : {}",
                                flowResult.getStatus().name());
                        throw new SystemException(CoreMessages.CORE_ERR_0067, flowResult.getStatus().name());
                    case WAIT:
                    case RUNNING:
                        // 中間ステータスであるため、故障の可能性が高い
                        // 基本的に中間ステータスでシナリオ実行処理まで返却されることはない想定
                        log.debug("invalid flow status in ScenarioRunnerImpls. status : {}",
                                flowResult.getStatus().name());
                        throw new SystemException(CoreMessages.CORE_ERR_0066, flowResult.getStatus().name());
                    case SUCCESS:
                    case WARN:
                        log.debug("flow execute success or warn.");
                        break;
                    default:
                        throw new SystemException(CoreMessages.CORE_ERR_0001);
                    }
                }

                if (exitFlg) {
                    // 即時終了のためループを抜ける
                    break;
                }

                // Flowの実行処理を解決
                var runner = FlowRunnerResolverImpl.getInstance().getFlowRunner(flow.getType());

                // バインド
                bind(context, executeContext, executeScenario, flow);

                // 実行
                flowResult = runner.execute(context, executeContext, executeScenario, flow);

            }

            // 終了判定
            if (executeScenario.hasFlowError()) {
                log.debug("Flow Error Occurred...");
                // シナリオエラー
                executeScenario.setStatus(ScenarioExecuteStatus.ERROR);
            } else if (executeScenario.hasCommandError()) {
                log.debug("Command Error Occurred...");
                // シナリオエラー
                executeScenario.setStatus(ScenarioExecuteStatus.ERROR);
            } else if (executeScenario.hasAssertError()) {
                log.debug("Assert Error Occurred...");
                // シナリオアサートエラー
                executeScenario.setStatus(ScenarioExecuteStatus.ASSERT_ERROR);
                // アサートエラーの場合は、次のコマンドも実施する
            } else if (executeScenario.getStatus() == ScenarioExecuteStatus.WAIT
                    || executeScenario.getStatus() == ScenarioExecuteStatus.RUNNING) {
                // 正常終了
                executeScenario.setStatus(ScenarioExecuteStatus.SUCCESS);
            }

        } catch (Throwable t) {
            log.error("Error Occurred...", t);

            // 発生したエラーを設定
            executeScenario.addNotification(ET3Notification.builder()
                    .stage(executeContext.getStage())
                    .error(t)
                    .level(NotificationType.ERROR)
                    .message(t.getMessage())
                    .build());

            // シナリオ失敗
            executeScenario.setStatus(ScenarioExecuteStatus.ERROR);

        } finally {

            // 掃除
            cleanScenarioVariables(context, executeScenario, et3);

            // シナリオ実行終了時間を設定
            executeScenario.setEnd(LocalDateTime.now());

            // 所用時間を設定
            executeScenario.setDuration(Duration.between(executeScenario.getStart(), executeScenario.getEnd()));

            // シナリオ終了ログ出力
            outputEndScenarioLog(context, executeScenario);

            // レポート出力
            if (!executeScenario.getOption().getNoreport()) {
                report(context, executeContext, executeScenario, error);
            }
        }

    }

    /**
     * シナリオ実行毎にグローバル変数の再設定を行う.<br>
     * グローバル変数は、他シナリオで書き換えられても元に戻す必要がある.<br>
     * 他シナリオで追加されたグローバル変数をクリアする必要はないため、再設定処理とする.
     *
     * @param context コンテキスト
     * @param executeContext 実行コンテキスト
     */
    private void reSettingGlobalVariables(final Context context, final ExecuteContext executeContext) {
        context.getOriginal().getGlobalVariables().forEach((k, v) -> executeContext.getGlobalVariables().put(k, v));
    }

    /**
     * 実行時に指定されたプロファイルを元に、実行コンテキストに設定する.
     *
     * @param context コンテキスト
     * @param executeScenario 実行コンテキスト
     */
    private void setProfiles(final Context context, final ExecuteScenario executeScenario) {

        if (StringUtils.isNotEmpty(executeScenario.getOption().getProfile())) {
            // プロファイルを抽出
            Arrays.stream(executeScenario.getOption().getProfile().split(",")).forEach(x -> {
                if (context.getOriginal().getProfiles().containsKey(x)) {
                    executeScenario.getProfileConstants().putAll(context.getOriginal().getProfiles().get(x));
                } else {
                    // 起動時に指定されたプロファイルが、
                    // シナリオの中に存在しないため実質有効ではないことをWARNログにて通知
                    log.warn(MessageManager.getInstance()
                            .getMessage(CoreMessages.CORE_WRN_0002, context.getOption().getProfile()));
                }
            });
        }

    }

    /**
     * Flowに対して、変数をバインドする.
     *
     * @param context コンテキスト
     * @param executeContext 実行コンテキスト
     * @param executeScenario シナリオ実行時情報
     * @param flow Flow
     */
    private void bind(final Context context, final ExecuteContext executeContext, final ExecuteScenario executeScenario,
            final Flow flow) {

        BindUtils.getInstance()
                .bind(flow, executeScenario.getProfileConstants(), executeContext.getGlobalVariables(),
                        executeScenario.getScenarioVariables(), null);
    }

    /**
     * シナリオスコープの変数を設定する.
     *
     * @param context コンテキスト
     * @param executeScenario 実行シナリオ
     * @param et3 シナリオ
     */
    private void settingScenarioVariables(final Context context, final ExecuteScenario executeScenario,
            final ET3Base et3) {

        // 自シナリオファイルに設定されているシナリオスコープ変数を設定
        if (et3.getVariables() != null && et3.getVariables().getScenario() != null) {
            et3.getVariables().getScenario().forEach((k, v) -> executeScenario.getScenarioVariables().put(k, v));
        }

        executeScenario.getScenarioVariables()
                .put(ScenarioScopeVariables.SCENARIO_DIR.getName(),
                        context.getOriginal().getScenarioPlacePaths().get(executeScenario.getInfo().getId()));
        executeScenario.getScenarioVariables()
                .put(ScenarioScopeVariables.EVIDENCE_DIR.getName(), executeScenario.getEvidencePath());
        executeScenario.getScenarioVariables()
                .put(ScenarioScopeVariables.CURRENT_SCENARIO.getName(), executeScenario.getFqsn());
    }

    /**
     * シナリオスコープの変数を掃除する.
     *
     * @param context コンテキスト
     * @param executeScenario 実行シナリオ
     * @param et3 シナリオ
     */
    private void cleanScenarioVariables(final Context context, final ExecuteScenario executeScenario,
            final ET3Base et3) {
        if (et3.getVariables() != null && et3.getVariables().getScenario() != null) {
            et3.getVariables().getScenario().forEach((k, v) -> executeScenario.getScenarioVariables().remove(k));
        }
        executeScenario.getScenarioVariables().remove(ScenarioScopeVariables.SCENARIO_DIR.getName());
        executeScenario.getScenarioVariables().remove(ScenarioScopeVariables.EVIDENCE_DIR.getName());
        executeScenario.getScenarioVariables().remove(ScenarioScopeVariables.CURRENT_SCENARIO.getName());
        executeScenario.getScenarioVariables().forEach((key, value) -> {
            if (key.contains(ExecuteScenario.FLOW_START_VARIABLE_SUFFIX)
                    || key.contains(ExecuteScenario.FLOW_END_VARIABLE_SUFFIX)) {
                executeScenario.getScenarioVariables().remove(key);
            }
        });
    }

    /**
     * 結果ディレクトリが未作成であった場合に、作成する.
     *
     * @param context コンテキスト
     */
    private void createResultDirectory(final Context context, final ExecuteContext executeContext,
            final ExecuteScenario scenario) {
        ExecutionFileUtils.createResultDirectory(context, executeContext);
    }

    /**
     * レポート出力.
     *
     * @param context コンテキスト
     * @param executeContext 実行情報
     * @param executeScenario シナリオ実行情報
     * @param t エラー
     */
    private void report(final Context context, final ExecuteContext executeContext,
            final ExecuteScenario executeScenario, final Throwable t) {
        ScenarioReporterImpl.getInstance().report(context, executeContext, executeScenario, t);
    }

    /**
     * シナリオ開始ログ出力.
     *
     * @param context
     * @param scenario
     */
    protected void outputStartScenarioLog(final Context context, final ExecuteScenario scenario) {
        log.info("######################################################################################");
        log.info("Start Scenario.");
        log.info("Scenario ID         : {}", scenario.getInfo().getId());
        log.info("Execute Scenario ID : {}", scenario.getExecuteScenarioId());
        log.info("######################################################################################");
    }

    /**
     * シナリオ終了ログ出力.
     *
     * @param context
     * @param scenario
     */
    protected void outputEndScenarioLog(final Context context, final ExecuteScenario scenario) {
        if (scenario.getStatus() == ScenarioExecuteStatus.SUCCESS) {
            log.info("######################################################################################");
            log.info("End Scenario.");
            log.info("Status              : Success");
            log.info("Scenario ID         : {}", scenario.getInfo().getId());
            log.info("Execute Scenario ID : {}", scenario.getExecuteScenarioId());
            log.info("######################################################################################");
        } else if (scenario.getStatus() == ScenarioExecuteStatus.ERROR) {
            log.error("######################################################################################");
            log.error("End Scenario.");
            log.error("Status              : Error");
            log.error("Scenario ID         : {}", scenario.getInfo().getId());
            log.error("Execute Scenario ID : {}", scenario.getExecuteScenarioId());
            log.error("######################################################################################");
        } else if (scenario.getStatus() == ScenarioExecuteStatus.ASSERT_ERROR) {
            log.error("######################################################################################");
            log.error("End Scenario.");
            log.error("Status              : Assert Error");
            log.error("Scenario ID         : {}", scenario.getInfo().getId());
            log.error("Execute Scenario ID : {}", scenario.getExecuteScenarioId());
            log.error("######################################################################################");
        } else {
            log.warn("######################################################################################");
            log.warn("End Scenario.");
            log.warn("Scenario ID         : {}", scenario.getInfo().getId());
            log.warn("Execute Scenario ID : {}", scenario.getExecuteScenarioId());
            log.warn("######################################################################################");
        }

    }

}
