/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.flow.runner.impl;

import com.epion_t3.core.common.bean.ExecuteFlow;
import com.epion_t3.core.common.bean.ExecuteScenario;
import com.epion_t3.core.common.bean.scenario.Flow;
import com.epion_t3.core.common.bean.scenario.HasChildrenFlow;
import com.epion_t3.core.common.context.Context;
import com.epion_t3.core.common.context.ExecuteContext;
import com.epion_t3.core.common.type.FlowStatus;
import com.epion_t3.core.common.type.ScenarioExecuteStatus;
import com.epion_t3.core.exception.SystemException;
import com.epion_t3.core.flow.bean.FlowResult;
import com.epion_t3.core.flow.resolver.impl.FlowRunnerResolverImpl;
import com.epion_t3.core.message.impl.CoreMessages;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 子Flowを実行するメソッドを保有する抽象クラス.
 *
 * @param <FLOW> Flow
 */
@Slf4j
public abstract class AbstractChildrenExecuteFlowRunner<FLOW extends Flow> extends AbstractBaseFlowRunner<FLOW> {

    /**
     * 子Flowを実行します.
     *
     * @param context コンテキスト
     * @param executeContext 実行コンテキスト
     * @param executeScenario 実行シナリオ
     * @param parentExecuteFlow 親実行Flow
     * @param flow Flow
     */
    protected void executeChildren(@NonNull Context context, @NonNull ExecuteContext executeContext,
            @NonNull ExecuteScenario executeScenario, @NonNull ExecuteFlow parentExecuteFlow,
            @NonNull HasChildrenFlow flow) {

        var childFlowResult = (FlowResult) null;

        // 現状マルチスレッドで動かす想定がない（AtomicBooleanは使用しない）
        var exitFlg = false;

        // ループを抜けるかの判定フラグ
        var breakLoopFlg = false;

        // 全てのフローを実行
        for (Flow childFlow : flow.getChildren()) {

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
                        log.debug("does not match execute flow id : {}. -> SKIP", childFlowResult.getChoiceId());
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
                    breakLoopFlg = true;
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

            if (exitFlg || breakLoopFlg) {
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

    }

}
