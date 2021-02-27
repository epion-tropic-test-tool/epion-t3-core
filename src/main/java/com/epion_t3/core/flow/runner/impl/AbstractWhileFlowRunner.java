/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.flow.runner.impl;

import com.epion_t3.core.common.bean.ExecuteFlow;
import com.epion_t3.core.common.bean.ExecuteScenario;
import com.epion_t3.core.common.bean.scenario.AbstractWhileFlow;
import com.epion_t3.core.common.bean.scenario.Flow;
import com.epion_t3.core.common.context.Context;
import com.epion_t3.core.common.context.ExecuteContext;
import com.epion_t3.core.common.type.FlowStatus;
import com.epion_t3.core.common.util.ErrorUtils;
import com.epion_t3.core.exception.SystemException;
import com.epion_t3.core.flow.bean.FlowResult;
import com.epion_t3.core.flow.logging.factory.FlowLoggerFactory;
import com.epion_t3.core.flow.logging.holder.FlowLoggingHolder;
import com.epion_t3.core.flow.resolver.impl.FlowRunnerResolverImpl;
import com.epion_t3.core.flow.runner.IterateTypeFlowRunner;
import com.epion_t3.core.message.impl.CoreMessages;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public abstract class AbstractWhileFlowRunner<FLOW extends AbstractWhileFlow>
        extends AbstractChildrenExecuteFlowRunner<FLOW> implements IterateTypeFlowRunner {

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
        var start = LocalDateTime.now();
        executeFlow.setStart(start);

        // タイムアウト時間を設定
        var timeout = start.plus(flow.getTimeout(), ChronoUnit.MILLIS);

        String startTimeKey = flow.getId() + ExecuteScenario.FLOW_START_VARIABLE_SUFFIX;
        if (!executeScenario.getScenarioVariables().containsKey(startTimeKey)) {
            executeScenario.getScenarioVariables().put(startTimeKey, new ArrayList<>());
        }
        ((List) executeScenario.getScenarioVariables().get(startTimeKey)).add(start);

        // 実装側には、ループ継続判定処理のみを実装させるため本クラスで結果の制御が必要
        // 生成も本クラスで行う
        var flowResult = FlowResult.getDefault();
        flowResult.setStatus(FlowStatus.RUNNING);

        try {

            // Flow開始ログ出力
            outputStartFlowLog(context, executeScenario, executeFlow);

            // Flowスコープ変数の設定
            settingFlowVariables(context, executeContext, executeScenario, executeFlow);

            // バインド
            bind(context, executeContext, executeScenario, executeFlow, flow);

            while (evaluation(context, executeContext, executeScenario, executeFlow, flow, logger)) {

                // 繰り返し処理
                executeChildren(context, executeContext, executeScenario, executeFlow, flow);

                if (Optional.ofNullable(executeScenario.getFlows())
                        .map(Collection::stream)
                        .orElseGet(Stream::empty)
                        // 繰り返し系コマンド自体のFlowResultはまだ設定されていない可能性があるため除外
                        // 複数ネストしている場合も同様となる
                        .filter(x -> x.getFlowResult() != null)
                        .anyMatch(x -> x.getFlowResult().getStatus() == FlowStatus.FORCE_EXIT)) {
                    break;
                }

                // 自Flow以降のFlowを全て抽出
                final AtomicBoolean findMe = new AtomicBoolean(false);
                var afterList = Optional.ofNullable(executeScenario.getFlows())
                        .map(Collection::stream)
                        .orElseGet(Stream::empty)
                        .filter(x -> {
                            if (!findMe.get()) {
                                findMe.set(x.getExecuteId().equals(executeFlow.getExecuteId()));
                                return false;
                            } else {
                                return true;
                            }
                        })
                        .collect(Collectors.toList());

                // 自Flow以降でIterateTypeのFlowが出るまでのFlowを全て抽出
                final AtomicBoolean findIterateTypeAfterMe = new AtomicBoolean(false);
                var findIterateTypeAfterMeList = Optional.ofNullable(afterList)
                        .map(Collection::stream)
                        .orElseGet(Stream::empty)
                        .filter(x -> {
                            if (!findIterateTypeAfterMe.get()) {
                                var runner = FlowRunnerResolverImpl.getInstance().getFlowRunner(x.getFlow().getType());
                                if (runner.getClass().isAssignableFrom(IterateTypeFlowRunner.class)) {
                                    findIterateTypeAfterMe.set(true);
                                    return false;
                                } else {
                                    return true;
                                }
                            } else {
                                return false;
                            }
                        })
                        .collect(Collectors.toList());

                // 自Flow以降でIterateTypeのFlowが出るまでの間に、FlowStatusがBreakがあれば、
                // このWhileループはBreakするべきだと判断してループを抜ける。
                if (Optional.ofNullable(findIterateTypeAfterMeList)
                        .map(Collection::stream)
                        .orElseGet(Stream::empty)
                        .anyMatch(x -> x.getFlowResult().getStatus() == FlowStatus.BREAK)) {
                    break;
                }

                // タイムアウト判定
                if (LocalDateTime.now().isAfter(timeout)) {
                    logger.error("flow timeout occurred...");
                    throw new SystemException(CoreMessages.CORE_ERR_0065, flow.getId(), flow.getTimeout());
                }

            }

            // 正常終了
            flowResult.setStatus(FlowStatus.SUCCESS);

        } catch (Throwable t) {

            // 解析用
            log.debug("Error Occurred...", t);

            // Flow失敗
            flowResult.setStatus(FlowStatus.ERROR);

            // 発生したエラーを設定
            executeFlow.setError(t);
            executeFlow.setStackTrace(ErrorUtils.getInstance().getStacktrace(t));

            // エラー処理
            onError(context, executeContext, executeScenario, executeFlow, flow, t, logger);

        } finally {

            // Flow結果を設定
            executeFlow.setFlowResult(flowResult);

            // 掃除
            cleanFlowVariables(context, executeContext, executeScenario, executeFlow);

            // シナリオ実行終了時間を設定
            LocalDateTime end = LocalDateTime.now();
            executeFlow.setEnd(end);
            var endTimeKey = flow.getId() + executeScenario.FLOW_END_VARIABLE_SUFFIX;
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
     * 繰り返し処理を継続するかを判定する評価式を実行します.
     *
     * @param context
     * @param executeContext
     * @param executeScenario
     * @param executeFlow
     * @param flow
     * @param logger
     * @return
     */
    protected abstract boolean evaluation(@NonNull Context context, @NonNull ExecuteContext executeContext,
            @NonNull ExecuteScenario executeScenario, @NonNull ExecuteFlow executeFlow, @NonNull FLOW flow,
            @NonNull Logger logger);

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
