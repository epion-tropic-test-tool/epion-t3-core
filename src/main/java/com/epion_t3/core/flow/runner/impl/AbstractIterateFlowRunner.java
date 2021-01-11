/* Copyright (c) 2017-2019 Nozomu Takashima. */
package com.epion_t3.core.flow.runner.impl;

import com.epion_t3.core.common.bean.ET3Notification;
import com.epion_t3.core.common.bean.ExecuteFlow;
import com.epion_t3.core.common.bean.ExecuteScenario;
import com.epion_t3.core.common.bean.scenario.Flow;
import com.epion_t3.core.common.bean.scenario.IterateFlow;
import com.epion_t3.core.common.context.Context;
import com.epion_t3.core.common.context.ExecuteContext;
import com.epion_t3.core.common.type.FlowScopeVariables;
import com.epion_t3.core.common.type.FlowStatus;
import com.epion_t3.core.common.type.ScenarioExecuteStatus;
import com.epion_t3.core.common.type.ScenarioScopeVariables;
import com.epion_t3.core.common.util.BindUtils;
import com.epion_t3.core.common.util.ErrorUtils;
import com.epion_t3.core.exception.SystemException;
import com.epion_t3.core.flow.bean.FlowResult;
import com.epion_t3.core.flow.logging.bean.FlowLog;
import com.epion_t3.core.flow.logging.factory.FlowLoggerFactory;
import com.epion_t3.core.flow.logging.holder.FlowLoggingHolder;
import com.epion_t3.core.flow.resolver.impl.FlowRunnerResolverImpl;
import com.epion_t3.core.flow.runner.FlowRunner;
import com.epion_t3.core.message.impl.CoreMessages;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 繰り返しFlowの基底クラス.
 *
 * @param <EXECUTE_CONTEXT>
 * @param <EXECUTE_SCENARIO>
 * @param <EXECUTE_FLOW>
 * @param <FLOW>
 */
@Slf4j
public abstract class AbstractIterateFlowRunner<EXECUTE_CONTEXT extends ExecuteContext, EXECUTE_SCENARIO extends ExecuteScenario, EXECUTE_FLOW extends ExecuteFlow, FLOW extends IterateFlow>
        implements FlowRunner<Context, EXECUTE_CONTEXT, EXECUTE_SCENARIO, FLOW> {

    /**
     * ロギングマーカー.
     * @since 0.0.4
     */
    private Marker collectLoggingMarker;

    /**
     * {@inheritDoc}
     */
    @Override
    public FlowResult execute(Context context, EXECUTE_CONTEXT executeContext, EXECUTE_SCENARIO executeScenario,
                              FLOW flow) {

        // process実行情報を作成
        var executeFlow = getExecuteFlowInstance();
        executeScenario.getFlows().add(executeFlow);
        executeFlow.setFlow(flow);

        // マーカーを生成
        collectLoggingMarker = MarkerFactory.getMarker(executeFlow.getExecuteId().toString());

        // Logger生成
        var logger = FlowLoggerFactory.getInstance().getFlowLogger(this.getClass());

        // Flow実行開始時間を設定
        LocalDateTime start = LocalDateTime.now();
        executeFlow.setStart(start);
        String startTimeKey = flow.getId() + executeScenario.FLOW_START_VARIABLE_SUFFIX;
        if (!executeScenario.getScenarioVariables().containsKey(startTimeKey)) {
            executeScenario.getScenarioVariables().put(startTimeKey, new ArrayList<>());
        }
        ((List) executeScenario.getScenarioVariables().get(startTimeKey)).add(start);

        FlowResult flowResult = null;

        try {

            // Flow開始ログ出力
            outputStartFlowLog(context, executeScenario, executeFlow);

            // Flowスコープ変数の設定
            settingFlowVariables(context, executeContext, executeScenario, executeFlow);

            // バインド
            bind(context, executeContext, executeScenario, executeFlow, flow);

            // ループ対象の解決
            var iterateTarget = resolveIterateTarget(context, executeContext, executeScenario, executeFlow, flow,
                    logger);

            // ループ処理
            loopProcess(context, executeContext, executeScenario, executeFlow, flow, logger, iterateTarget);

            // 正常終了
            executeFlow.setStatus(FlowStatus.SUCCESS);

        } catch (Throwable t) {

            // 解析用
            log.debug("Error Occurred...", t);

            // 発生したエラーを設定
            executeFlow.setError(t);
            executeFlow.setStackTrace(ErrorUtils.getInstance().getStacktrace(t));

            // プロセス失敗
            executeFlow.setStatus(FlowStatus.ERROR);

            // エラー処理
            onError(context, executeContext, executeScenario, executeFlow, flow, t, logger);

        } finally {

            // 掃除
            cleanFlowVariables(context, executeContext, executeScenario, executeFlow);

            // シナリオ実行終了時間を設定
            LocalDateTime end = LocalDateTime.now();
            executeFlow.setEnd(end);
            String endTimeKey = flow.getId() + executeScenario.FLOW_END_VARIABLE_SUFFIX;
            if (!executeScenario.getScenarioVariables().containsKey(endTimeKey)) {
                executeScenario.getScenarioVariables().put(endTimeKey, new ArrayList<>());
            }
            ((List) executeScenario.getScenarioVariables().get(endTimeKey)).add(end);

            // 所用時間を設定
            executeFlow.setDuration(Duration.between(executeFlow.getStart(), executeFlow.getEnd()));

            // プロセスのログを収集
            List<FlowLog> flowLogs = FlowLoggingHolder.get(executeFlow.getExecuteId().toString());
            executeFlow.setFlowLogs(flowLogs);

            // プロセスのログは収集し終えたらクリアする（ThreadLocalにて保持）
            FlowLoggingHolder.clear(executeFlow.getExecuteId().toString());

            // プロセス終了ログ出力
            outputEndFlowLog(context, executeScenario, executeFlow);

            // エラー処理
            onFinally(context, executeContext, executeScenario, executeFlow, flow, logger);

        }

        return flowResult;
    }

    /**
     * 収集対象のロギングマーカーを取得します.
     * @since 0.0.4
     * @return ロギングマーカー
     */
    protected Marker collectLoggingMarker() {
        return this.collectLoggingMarker;
    }

    /**
     * ループ処理対象の反復要素を解決します.
     *
     * @param context
     * @param execute_context
     * @param executeScenario
     * @param executeFlow
     * @param flow
     * @param logger
     * @return
     */
    protected abstract Iterable resolveIterateTarget(Context context, EXECUTE_CONTEXT execute_context,
                                                     EXECUTE_SCENARIO executeScenario, EXECUTE_FLOW executeFlow, FLOW flow, Logger logger);

    /**
     * ループ処理を行います.
     *
     * @param context
     * @param executeContext
     * @param executeScenario
     * @param parentExecuteFlow
     * @param parentFlow
     * @param logger
     * @param iterateTarget
     */
    private void loopProcess(@NonNull Context context, @NonNull EXECUTE_CONTEXT executeContext,
                             @NonNull EXECUTE_SCENARIO executeScenario, @NonNull EXECUTE_FLOW parentExecuteFlow,
                             @NonNull FLOW parentFlow, @NonNull Logger logger, @NonNull Iterable iterateTarget) {

        // 解決したコレクションの数だけ回す
        for (var target : iterateTarget) {

            // 繰り返し対象オブジェクトを設定（シナリオスコープに設定）
            executeScenario.getScenarioVariables().put(ScenarioScopeVariables.CURRENT_ITERATE_TARGET.getName(), target);

            var childFlowResult = (FlowResult) null;

            // 現状マルチスレッドで動かす想定がない（AtomicBooleanは使用しない）
            boolean exitFlg = false;

            // 全てのフローを実行
            for (Flow childFlow : parentFlow.getChildren()) {

                if (childFlowResult != null) {
                    // 前Flowの結果によって処理を振り分ける
                    switch (childFlowResult.getStatus()) {
                        case NEXT:
                            // 単純に次のFlowへ遷移
                            log.debug("Execute Next Flow.");
                            break;
                        case CHOICE:
                            log.debug("choice execute next flow.");
                            // 指定された後続Flowへ遷移
                            if (StringUtils.equals(childFlowResult.getChoiceId(), childFlow.getId())) {
                                // 合致したため実行する
                                log.debug("match flow id : {} -> NEXT.", childFlowResult.getChoiceId());
                            } else {
                                // SKIP扱いとする
                                log.debug("does not match execute flow id : {}. -> SKIP",
                                        childFlowResult.getChoiceId());
                                var childExecuteFlow = new ExecuteFlow();
                                childExecuteFlow.setStatus(FlowStatus.SKIP);
                                childExecuteFlow.setFlow(childFlow);
                                executeScenario.getFlows().add(childExecuteFlow);
                                // 次のループまで
                                continue;
                            }
                            break;
                        case EXIT:
                            // 即時終了
                            log.debug("force exit scenario.");
                            exitFlg = true;
                            break;
                        case BREAK:
                            // ループ用終了ステータス
                            log.debug("force exit scenario.");
                            break;
                        case CONTINUE:
                            // ループ用継続ステータス
                            log.debug("force exit scenario.");
                            continue;
                        default:
                            // TODO:Error
                            throw new SystemException(CoreMessages.CORE_ERR_0001);
                    }
                }

                if (exitFlg) {
                    // 即時終了のためループを抜ける
                    break;
                }

                // Flowの実行処理を解決
                var runner = FlowRunnerResolverImpl.getInstance().getFlowRunner(childFlow.getType());

                // バインド
                bind(context, executeContext, executeScenario, parentExecuteFlow, childFlow);

                // 実行
                childFlowResult = runner.execute(context, executeContext, executeScenario, childFlow);

                var childExecuteFlow = executeScenario.getFlows().get(executeScenario.getFlows().size() - 1);

                // 終了判定
                if (childExecuteFlow.getStatus() == FlowStatus.ERROR) {
                    log.debug("Error Occurred...");
                    // シナリオエラー
                    executeScenario.setStatus(ScenarioExecuteStatus.ERROR);
                    break;
                } else if (childExecuteFlow.getStatus() == FlowStatus.ASSERT_ERROR) {
                    log.debug("Assert Error Occurred...");
                    // シナリオアサートエラー
                    executeScenario.setStatus(ScenarioExecuteStatus.ASSERT_ERROR);
                    // アサートエラーの場合は、次のコマンドも実施する
                }

            }

            // 繰り返し対象オブジェクトを削除（シナリオスコープに設定）残ってもOK.
            executeScenario.getScenarioVariables().remove(ScenarioScopeVariables.CURRENT_ITERATE_TARGET.getName());
        }

    }

    /**
     * @return
     */
    private EXECUTE_FLOW getExecuteFlowInstance() {
//        try {
//            Class<?> clazz = this.getClass();
//            Type type = clazz.getGenericSuperclass();
//            ParameterizedType pt = (ParameterizedType) type;
//            Type[] actualTypeArguments = pt.getActualTypeArguments();
//            Class<?> entityClass = (Class<?>) actualTypeArguments[2];
//            return (EXECUTE_FLOW) entityClass.newInstance();
            return (EXECUTE_FLOW) new ExecuteFlow();
//        } catch (ReflectiveOperationException e) {
//            // TODO:ErrorProcess
//            throw new RuntimeException(e);
//        }
    }

    /**
     * Flowに対して、変数をバインドする.
     *
     * @param context         コンテキスト
     * @param executeScenario シナリオ実行情報
     * @param executeFlow     Flow実行情報
     * @param flow            Flow
     */
    private void bind(final Context context, final EXECUTE_CONTEXT executeContext,
                      final EXECUTE_SCENARIO executeScenario, final EXECUTE_FLOW executeFlow, final Flow flow) {

        BindUtils.getInstance()
                .bind(flow, executeScenario.getProfileConstants(), executeContext.getGlobalVariables(),
                        executeScenario.getScenarioVariables(), executeFlow.getFlowVariables());
    }

    /**
     * エラー処理を行う. この処理は、Flowの処理結果が失敗の場合に実行される.
     *
     * @param context         コンテキスト
     * @param executeScenario シナリオ実行情報
     * @param executeFlow     Flow実行情報
     * @param flow            Flow
     * @param t               例外
     */
    protected void onError(Context context, EXECUTE_CONTEXT execute_context, EXECUTE_SCENARIO executeScenario,
                           EXECUTE_FLOW executeFlow, FLOW flow, Throwable t, Logger logger) {
        // 必要に応じてオーバーライド実装すること.
    }

    /**
     * エラー処理を行う. この処理は、子Flowの処理結果が失敗の場合に実行される.
     *
     * @param context         コンテキスト
     * @param executeScenario シナリオ実行情報
     * @param executeFlow     Flow実行情報
     * @param flow            Flow
     * @param t               例外
     */
    protected void onChildError(final Context context, final EXECUTE_CONTEXT executeContext,
                                final EXECUTE_SCENARIO executeScenario, final EXECUTE_FLOW executeFlow, final FLOW flow,
                                final EXECUTE_FLOW childExecuteFlow, final Flow childFlow, final Throwable t, final Logger logger) {
        // 必要に応じてオーバーライド実装すること.
    }

    /**
     * 終了処理を行う. この処理は、Flowの処理結果が成功・失敗に関わらず実行される.
     *
     * @param context         コンテキスト
     * @param executeScenario シナリオ実行情報
     * @param executeFlow     Flow実行情報
     * @param flow            Flow
     */
    protected void onFinally(final Context context, final EXECUTE_CONTEXT executeContext,
                             final EXECUTE_SCENARIO executeScenario, final EXECUTE_FLOW executeFlow, final FLOW flow,
                             final Logger logger) {
        // 必要に応じてオーバーライド実装すること.
    }

    /**
     * 終了処理を行う. この処理は、子Flowの処理結果が成功・失敗に関わらず実行される.
     *
     * @param context         コンテキスト
     * @param executeScenario シナリオ実行情報
     * @param executeFlow     Flow実行情報
     * @param flow            Flow
     */
    protected void onChildFinally(final Context context, final EXECUTE_CONTEXT execute_context,
                                  final EXECUTE_SCENARIO executeScenario, final EXECUTE_FLOW executeFlow, final FLOW flow,
                                  final EXECUTE_FLOW childExecuteFlow, final Flow childFlow, final Logger logger) {
        // 必要に応じてオーバーライド実装すること.
    }

    /**
     * シナリオスコープの変数を設定する. プロセス実行時に指定を行うべきシナリオスコープ変数の設定処理.
     *
     * @param context         コンテキスト
     * @param executeScenario シナリオ実行情報
     * @param executeFlow     FLOW実行情報
     */
    private void settingFlowVariables(final Context context, final EXECUTE_CONTEXT execute_context,
                                      final EXECUTE_SCENARIO executeScenario, final ExecuteFlow executeFlow) {

        // 現在の処理Flow
        executeFlow.getFlowVariables().put(FlowScopeVariables.CURRENT_FLOW.getName(), executeFlow.getFlow().getId());

        // 現在の処理FLOWの実行ID
        executeFlow.getFlowVariables()
                .put(FlowScopeVariables.CURRENT_FLOW_EXECUTE_ID.getName(), executeFlow.getExecuteId());
    }

    /**
     * シナリオスコープの変数を掃除する. プロセス実行時にのみ指定すべきシナリオスコープの変数を確実に除去するための処理.
     *
     * @param context         コンテキスト
     * @param executeScenario シナリオ実行情報
     * @param executeFlow     FLOW実行情報
     */
    private void cleanFlowVariables(final Context context, final EXECUTE_CONTEXT executeContext,
                                    final EXECUTE_SCENARIO executeScenario, final ExecuteFlow executeFlow) {

        // 現在の処理Flow
        executeFlow.getFlowVariables().remove(FlowScopeVariables.CURRENT_FLOW.getName());

        // 現在の処理FLOWの実行ID
        executeFlow.getFlowVariables().remove(FlowScopeVariables.CURRENT_FLOW_EXECUTE_ID.getName());

    }

    /**
     * Flow開始ログ出力.
     *
     * @param context         コンテキスト
     * @param executeScenario 実行シナリオ
     * @param executeFlow     実行Flow
     */
    protected void outputStartFlowLog(Context context, ExecuteScenario executeScenario, ExecuteFlow executeFlow) {
        log.info("■ Start Flow    ■ Scenario ID : {}, Flow ID : {}", executeScenario.getInfo().getId(),
                executeFlow.getFlow().getId());
    }

    /**
     * Flow終了ログ出力.
     *
     * @param context         コンテキスト
     * @param executeScenario 実行シナリオ
     * @param executeFlow     実行Flow
     */
    protected void outputEndFlowLog(Context context, ExecuteScenario executeScenario, ExecuteFlow executeFlow) {
        if (executeFlow.getStatus() == FlowStatus.SUCCESS) {
            log.info("■ End Flow      ■ Scenario ID : {}, Flow ID : {}, Flow Status : {}",
                    executeScenario.getInfo().getId(), executeFlow.getFlow().getId(), executeFlow.getStatus().name());
        } else if (executeFlow.getStatus() == FlowStatus.ERROR) {
            log.error("■ End Flow      ■ Scenario ID : {}, Flow ID : {}, Flow Status : {}",
                    executeScenario.getInfo().getId(), executeFlow.getFlow().getId(), executeFlow.getStatus().name());
        } else {
            log.warn("■ End Flow      ■ Scenario ID : {}, Flow ID : {}, Flow Status : {}",
                    executeScenario.getInfo().getId(), executeFlow.getFlow().getId(), executeFlow.getStatus().name());
        }

    }

}
