package com.epion_t3.core.scenario.runner.impl;

import com.epion_t3.core.common.context.Context;
import com.epion_t3.core.common.bean.Option;
import com.epion_t3.core.common.context.ExecuteContext;
import com.epion_t3.core.common.bean.ExecuteFlow;
import com.epion_t3.core.common.bean.ExecuteScenario;
import com.epion_t3.core.common.type.StageType;
import com.epion_t3.core.exception.ScenarioNotFoundException;
import com.epion_t3.core.common.bean.ET3Notification;
import com.epion_t3.core.flow.model.FlowResult;
import com.epion_t3.core.flow.resolver.impl.FlowRunnerResolverImpl;
import com.epion_t3.core.flow.runner.FlowRunner;
import com.epion_t3.core.message.MessageManager;
import com.epion_t3.core.message.impl.CoreMessages;
import com.epion_t3.core.common.bean.scenario.Flow;
import com.epion_t3.core.common.bean.scenario.Scenario;
import com.epion_t3.core.common.bean.scenario.ET3Base;
import com.epion_t3.core.scenario.reporter.impl.ScenarioReporterImpl;
import com.epion_t3.core.scenario.runner.ScenarioRunner;
import com.epion_t3.core.common.type.FlowStatus;
import com.epion_t3.core.common.type.ScenarioExecuteStatus;
import com.epion_t3.core.common.type.ScenarioScopeVariables;
import com.epion_t3.core.common.util.BindUtils;
import com.epion_t3.core.common.util.ExecutionFileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

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
        ET3Base t3 = context.getOriginal().getOriginals().get(context.getOption().getTarget());

        if (t3 == null) {
            throw new ScenarioNotFoundException(context.getOption().getTarget());
        }

        if (t3.getScenarios().isEmpty()) {
            // 単シナリオ起動
            Scenario scenarioRef = new Scenario();
            scenarioRef.setRef(context.getOption().getTarget());
            scenarioRef.setProfile(context.getOption().getProfile());
            scenarioRef.setMode(context.getOption().getMode());
            scenarioRef.setNoreport(context.getOption().getNoreport());
            scenarioRef.setMode(context.getOption().getMode());
            executeScenario(context, executeContext, scenarioRef);
        } else {
            // 複数シナリオ起動
            for (Scenario scenarioRef : t3.getScenarios()) {
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

        ET3Base scenario = context.getOriginal().getOriginals().get(scenarioRef.getRef());

        if (scenario == null) {
            throw new ScenarioNotFoundException(scenarioRef.getRef());
        }

        // オプション解決
        Option option = SerializationUtils.clone(context.getOption());
        // プロファイルのオーバーライド
        if (StringUtils.isNotEmpty(scenarioRef.getProfile())
                && StringUtils.isNotEmpty(option.getProfile())
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

        ExecuteScenario executeScenario = new ExecuteScenario();
        executeScenario.setOption(option);
        executeScenario.setInfo(scenario.getInfo());
        executeScenario.setFqsn(scenario.getInfo().getId());
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
            settingScenarioVariables(context, executeScenario);

            FlowResult flowResult = null;

            boolean exitFlg = false;

            // 全てのフローを実行
            for (Flow flow : scenario.getFlows()) {

                if (flowResult != null) {
                    // 前Flowの結果によって処理を振り分ける
                    switch (flowResult.getStatus()) {
                        case NEXT:
                            // 単純に次のFlowへ遷移
                            log.debug("Execute Next Flow.");
                            break;
                        case CHOICE:
                            log.debug("Choice Execute Next Flow.");
                            // 指定された後続Flowへ遷移
                            if (StringUtils.equals(flowResult.getChoiceId(), flow.getId())) {
                                // 合致したため実行する
                                log.debug("Find To Be Executed Flow.");
                            } else {
                                // SKIP扱いとする
                                log.debug("Can't Find Execute Flow. -> SKIP");
                                // TODO:ちょっと微妙だな・・・
                                ExecuteFlow executeFlow = new ExecuteFlow();
                                executeFlow.setStatus(FlowStatus.SKIP);
                                executeFlow.setFlow(flow);
                                executeScenario.getFlows().add(executeFlow);
                                // 次のループまで
                                continue;
                            }
                            break;
                        case EXIT:
                            // 即時終了
                            log.debug("Force Exit Scenario.");
                            exitFlg = true;
                            break;
                    }
                }

                if (exitFlg) {
                    // 即時終了のためループを抜ける
                    break;
                }

                // Flowの実行処理を解決
                FlowRunner runner = FlowRunnerResolverImpl.getInstance().getFlowRunner(flow.getType());

                // バインド
                bind(context, executeContext, executeScenario, flow);

                // 実行
                flowResult = runner.execute(
                        context,
                        executeContext,
                        executeScenario,
                        flow,
                        LoggerFactory.getLogger("FlowLog"));

                ExecuteFlow executeFlow = executeScenario.getFlows().get(executeScenario.getFlows().size() - 1);

                // 終了判定
                if (executeScenario.getStatus() == ScenarioExecuteStatus.WAIT
                        || executeScenario.getStatus() == ScenarioExecuteStatus.RUNNING) {
                    executeScenario.setStatus(ScenarioExecuteStatus.SUCCESS);
                }
                if (executeFlow.getStatus() == FlowStatus.ERROR) {
                    log.debug("Error Occurred...");
                    // シナリオエラー
                    executeScenario.setStatus(ScenarioExecuteStatus.ERROR);
                    break;
                } else if (executeFlow.getStatus() == FlowStatus.ASSERT_ERROR) {
                    log.debug("Assert Error Occurred...");
                    // シナリオアサートエラー
                    executeScenario.setStatus(ScenarioExecuteStatus.ASSERT_ERROR);
                    // アサートエラーの場合は、次のコマンドも実施する
                }

            }

        } catch (Throwable t) {
            log.debug("Error Occurred...", t);

            // 発生したエラーを設定
            executeScenario.addNotification(
                    ET3Notification.builder().stage(executeContext.getStage()).error(t).message(t.getMessage()).build());

            // シナリオ失敗
            executeScenario.setStatus(ScenarioExecuteStatus.ERROR);

        } finally {

            // 掃除
            cleanScenarioVariables(context, executeScenario);

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
     * 実行時に指定されたプロファイルを元に、実行コンテキストに設定する.
     *
     * @param context         コンテキスト
     * @param executeScenario 実行コンテキスト
     */
    private void setProfiles(final Context context, final ExecuteScenario executeScenario) {

        if (StringUtils.isNotEmpty(executeScenario.getOption().getProfile())) {
            // プロファイルを抽出
            Arrays.stream(executeScenario.getOption().getProfile().split(","))
                    .forEach(x -> {
                        if (context.getOriginal().getProfiles().containsKey(x)) {
                            executeScenario.getProfileConstants().putAll(context.getOriginal().getProfiles().get(x));
                        } else {
                            // 起動時に指定されたプロファイルが、
                            // シナリオの中に存在しないため実質有効ではないことをWARNログにて通知
                            log.warn(
                                    MessageManager.getInstance().getMessage(
                                            CoreMessages.CORE_WRN_0002, context.getOption().getProfile()));
                        }
                    });
        }

    }


    /**
     * Flowに対して、変数をバインドする.
     *
     * @param context
     * @param executeScenario
     * @param flow
     */
    private void bind(final Context context,
                      final ExecuteContext executeContext,
                      final ExecuteScenario executeScenario,
                      final Flow flow) {

        BindUtils.getInstance().bind(
                flow,
                executeScenario.getProfileConstants(),
                executeContext.getGlobalVariables(),
                executeScenario.getScenarioVariables(),
                null);
    }


    /**
     * シナリオスコープの変数を設定する.
     *
     * @param context コンテキスト
     * @param executeScenario 実行シナリオ
     */
    private void settingScenarioVariables(final Context context, final ExecuteScenario executeScenario) {
        executeScenario.getScenarioVariables().put(
                ScenarioScopeVariables.SCENARIO_DIR.getName(),
                context.getOriginal().getScenarioPlacePaths().get(executeScenario.getInfo().getId()));
        executeScenario.getScenarioVariables().put(
                ScenarioScopeVariables.EVIDENCE_DIR.getName(),
                executeScenario.getEvidencePath());
        executeScenario.getScenarioVariables().put(
                ScenarioScopeVariables.CURRENT_SCENARIO.getName(),
                executeScenario.getFqsn());
    }

    /**
     * シナリオスコープの変数を掃除する.
     *
     * @param context コンテキスト
     * @param executeScenario 実行シナリオ
     */
    private void cleanScenarioVariables(final Context context, final ExecuteScenario executeScenario) {
        executeScenario.getScenarioVariables().remove(
                ScenarioScopeVariables.SCENARIO_DIR.getName());
        executeScenario.getScenarioVariables().remove(
                ScenarioScopeVariables.EVIDENCE_DIR.getName());
        executeScenario.getScenarioVariables().remove(
                ScenarioScopeVariables.CURRENT_SCENARIO.getName());
        executeScenario.getScenarioVariables().entrySet().forEach(x -> {
            if (x.getKey().contains(ExecuteScenario.FLOW_START_VARIABLE_SUFFIX)
                    || x.getKey().contains(ExecuteScenario.FLOW_END_VARIABLE_SUFFIX)) {
                executeScenario.getScenarioVariables().remove(x.getKey());
            }
        });
    }

    /**
     * 結果ディレクトリが未作成であった場合に、作成する.
     *
     * @param context
     */
    private void createResultDirectory(final Context context, final ExecuteContext executeContext, final ExecuteScenario scenario) {
        ExecutionFileUtils.createResultDirectory(context, executeContext);
    }

    /**
     * レポート出力.
     *
     * @param context         コンテキスト
     * @param executeContext  実行情報
     * @param executeScenario シナリオ実行情報
     * @param t               エラー
     */
    private void report(
            final Context context,
            final ExecuteContext executeContext,
            final ExecuteScenario executeScenario,
            final Throwable t) {
        ScenarioReporterImpl.getInstance().report(
                context, executeContext, executeScenario, t);
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
