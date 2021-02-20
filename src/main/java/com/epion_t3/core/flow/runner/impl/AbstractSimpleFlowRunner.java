/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.flow.runner.impl;

import com.epion_t3.core.common.bean.ExecuteCommand;
import com.epion_t3.core.common.bean.ExecuteFlow;
import com.epion_t3.core.common.bean.ExecuteScenario;
import com.epion_t3.core.common.bean.scenario.Flow;
import com.epion_t3.core.common.context.Context;
import com.epion_t3.core.common.context.ExecuteContext;
import com.epion_t3.core.common.type.FlowStatus;
import com.epion_t3.core.common.util.ErrorUtils;
import com.epion_t3.core.flow.bean.FlowResult;
import com.epion_t3.core.flow.logging.factory.FlowLoggerFactory;
import com.epion_t3.core.flow.logging.holder.FlowLoggingHolder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.Marker;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 単一Flowの基底クラス.
 *
 * @param <FLOW>
 */
@Slf4j
public abstract class AbstractSimpleFlowRunner<FLOW extends Flow> extends AbstractBaseFlowRunner<FLOW> {

    /**
     * ロギングマーカー.
     * 
     * @since 0.0.4
     */
    private Marker collectLoggingMarker;

    /**
     * {@inheritDoc}
     */
    @Override
    public FlowResult execute(Context context, ExecuteContext executeContext, ExecuteScenario executeScenario,
            FLOW flow) {

        // Flow実行情報を作成
        var executeFlow = createExecuteFlow();
        executeScenario.getFlows().add(executeFlow);
        executeFlow.setFlow(flow);

        // Logger生成
        var logger = FlowLoggerFactory.getInstance().getFlowLogger(this.getClass());

        // Flow実行開始時間を設定
        var start = LocalDateTime.now();
        executeFlow.setStart(start);
        var startTimeKey = flow.getId() + executeScenario.FLOW_START_VARIABLE_SUFFIX;
        if (!executeScenario.getScenarioVariables().containsKey(startTimeKey)) {
            executeScenario.getScenarioVariables().put(startTimeKey, new ArrayList<>());
        }
        ((List) executeScenario.getScenarioVariables().get(startTimeKey)).add(start);

        var flowResult = (FlowResult) null;

        try {

            // Flow開始ログ出力
            outputStartFlowLog(context, executeScenario, executeFlow);

            // Flowスコープ変数の設定
            settingFlowVariables(context, executeContext, executeScenario, executeFlow);

            // バインド
            bind(context, executeContext, executeScenario, executeFlow, flow);

            // 実行
            flowResult = execute(context, executeContext, executeScenario, executeFlow, flow, logger);

            if (executeFlow.hasCommandError()) {
                // コマンド失敗
                executeFlow.setStatus(FlowStatus.ERROR);
            } else {

                // プロセス成功
                executeFlow.setStatus(FlowStatus.SUCCESS);

                for (ExecuteCommand executeCommand : executeFlow.getCommands()) {
                    if (executeCommand.hasAssertError()) {
                        // コマンド失敗
                        executeFlow.setStatus(FlowStatus.ASSERT_ERROR);
                        break;
                    }
                }

            }

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
            var end = LocalDateTime.now();
            executeFlow.setEnd(end);
            var endTimeKey = flow.getId() + executeScenario.FLOW_END_VARIABLE_SUFFIX;
            if (!executeScenario.getScenarioVariables().containsKey(endTimeKey)) {
                executeScenario.getScenarioVariables().put(endTimeKey, new ArrayList<>());
            }
            ((List) executeScenario.getScenarioVariables().get(endTimeKey)).add(end);

            // 所用時間を設定
            executeFlow.setDuration(Duration.between(executeFlow.getStart(), executeFlow.getEnd()));

            // Flowのログを収集
            var flowLogs = FlowLoggingHolder.get(executeFlow.getExecuteId().toString());
            executeFlow.setFlowLogs(flowLogs);

            // Flowのログは収集し終えたらクリアする（ThreadLocalにて保持）
            FlowLoggingHolder.clear(executeFlow.getExecuteId().toString());

            // Flow終了ログ出力
            outputEndFlowLog(context, executeScenario, executeFlow);

            // エラー処理
            onFinally(context, executeContext, executeScenario, executeFlow, flow, logger);

        }

        return flowResult;
    }

    /**
     * Flowを実行します.
     * 
     * @param context コンテキスト
     * @param executeScenario シナリオ実行情報
     * @param executeFlow Flow実行情報
     * @param flow Flow
     */
    protected abstract FlowResult execute(Context context, ExecuteContext ExecuteContext,
            ExecuteScenario executeScenario, ExecuteFlow executeFlow, FLOW flow, Logger logger);

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
     * 終了処理を行う. この処理は、Flowの処理結果が成功・失敗に関わらず実行される.
     *
     * @param context コンテキスト
     * @param executeScenario シナリオ実行情報
     * @param executeFlow Flow実行情報
     * @param flow Flow
     */
    protected void onFinally(final Context context, final ExecuteContext ExecuteContext,
            final ExecuteScenario executeScenario, final ExecuteFlow executeFlow, final FLOW flow,
            final Logger logger) {
        // 必要に応じてオーバーライド実装すること.
    }

}
