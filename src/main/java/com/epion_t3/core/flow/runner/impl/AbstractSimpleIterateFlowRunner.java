/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.flow.runner.impl;

import com.epion_t3.core.common.bean.ExecuteFlow;
import com.epion_t3.core.common.bean.ExecuteScenario;
import com.epion_t3.core.common.bean.scenario.Flow;
import com.epion_t3.core.common.bean.scenario.HasChildrenFlow;
import com.epion_t3.core.common.context.Context;
import com.epion_t3.core.common.context.ExecuteContext;
import com.epion_t3.core.common.type.FlowStatus;
import com.epion_t3.core.common.type.ScenarioScopeVariables;
import com.epion_t3.core.common.util.ErrorUtils;
import com.epion_t3.core.flow.bean.FlowResult;
import com.epion_t3.core.flow.logging.factory.FlowLoggerFactory;
import com.epion_t3.core.flow.logging.holder.FlowLoggingHolder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 繰り返しFlowの基底クラス.
 *
 * @param <FLOW>
 */
@Slf4j
public abstract class AbstractSimpleIterateFlowRunner<FLOW extends HasChildrenFlow>
        extends AbstractChildrenExecuteFlowRunner<FLOW> {

    /**
     * {@inheritDoc}
     */
    @Override
    public FlowResult execute(Context context, ExecuteContext executeContext, ExecuteScenario executeScenario,
            FLOW flow) {

        // process実行情報を作成
        var executeFlow = createExecuteFlow();
        executeScenario.getFlows().add(executeFlow);
        executeFlow.setFlow(flow);

        // Logger生成
        var logger = FlowLoggerFactory.getInstance().getFlowLogger(this.getClass());

        // Flow実行開始時間を設定
        LocalDateTime start = LocalDateTime.now();
        executeFlow.setStart(start);
        String startTimeKey = flow.getId() + ExecuteScenario.FLOW_START_VARIABLE_SUFFIX;
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
            var flowLogs = FlowLoggingHolder.get(executeFlow.getExecuteId().toString());
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
     * ループ処理対象の反復要素を解決します.
     *
     * @param context
     * @param ExecuteContext
     * @param executeScenario
     * @param executeFlow
     * @param flow
     * @param logger
     * @return
     */
    protected abstract Iterable resolveIterateTarget(Context context, ExecuteContext ExecuteContext,
            ExecuteScenario executeScenario, ExecuteFlow executeFlow, FLOW flow, Logger logger);

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
    private void loopProcess(@NonNull Context context, @NonNull ExecuteContext executeContext,
            @NonNull ExecuteScenario executeScenario, @NonNull ExecuteFlow parentExecuteFlow, @NonNull FLOW parentFlow,
            @NonNull Logger logger, @NonNull Iterable iterateTarget) {

        // 解決したコレクションの数だけ回す
        for (var target : iterateTarget) {

            // 繰り返し対象オブジェクトを設定（シナリオスコープに設定）
            executeScenario.getScenarioVariables().put(ScenarioScopeVariables.CURRENT_ITERATE_TARGET.getName(), target);

            // 繰り返し処理
            executeChildren(context, executeContext, executeScenario, parentExecuteFlow, parentFlow);

            // 繰り返し対象オブジェクトを削除（シナリオスコープに設定）残ってもOK.
            executeScenario.getScenarioVariables().remove(ScenarioScopeVariables.CURRENT_ITERATE_TARGET.getName());
        }

    }

    /**
     * エラー処理を行う. この処理は、Flowの処理結果が失敗の場合に実行される.
     *
     * @param context コンテキスト
     * @param executeScenario シナリオ実行情報
     * @param executeFlow Flow実行情報
     * @param flow Flow
     * @param t 例外
     */
    protected void onError(Context context, ExecuteContext ExecuteContext, ExecuteScenario executeScenario,
            ExecuteFlow executeFlow, FLOW flow, Throwable t, Logger logger) {
        // 必要に応じてオーバーライド実装すること.
    }

    /**
     * エラー処理を行う. この処理は、子Flowの処理結果が失敗の場合に実行される.
     *
     * @param context コンテキスト
     * @param executeScenario シナリオ実行情報
     * @param executeFlow Flow実行情報
     * @param flow Flow
     * @param t 例外
     */
    protected void onChildError(final Context context, final ExecuteContext executeContext,
            final ExecuteScenario executeScenario, final ExecuteFlow executeFlow, final FLOW flow,
            final ExecuteFlow childExecuteFlow, final Flow childFlow, final Throwable t, final Logger logger) {
        // 必要に応じてオーバーライド実装すること.
    }

    /**
     * 終了処理を行う. この処理は、Flowの処理結果が成功・失敗に関わらず実行される.
     *
     * @param context コンテキスト
     * @param executeScenario シナリオ実行情報
     * @param executeFlow Flow実行情報
     * @param flow Flow
     */
    protected void onFinally(final Context context, final ExecuteContext executeContext,
            final ExecuteScenario executeScenario, final ExecuteFlow executeFlow, final FLOW flow,
            final Logger logger) {
        // 必要に応じてオーバーライド実装すること.
    }

    /**
     * 終了処理を行う. この処理は、子Flowの処理結果が成功・失敗に関わらず実行される.
     *
     * @param context コンテキスト
     * @param executeScenario シナリオ実行情報
     * @param executeFlow Flow実行情報
     * @param flow Flow
     */
    protected void onChildFinally(final Context context, final ExecuteContext ExecuteContext,
            final ExecuteScenario executeScenario, final ExecuteFlow executeFlow, final FLOW flow,
            final ExecuteFlow childExecuteFlow, final Flow childFlow, final Logger logger) {
        // 必要に応じてオーバーライド実装すること.
    }

}
