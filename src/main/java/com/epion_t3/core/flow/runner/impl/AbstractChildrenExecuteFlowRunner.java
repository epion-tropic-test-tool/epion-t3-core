/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.flow.runner.impl;

import com.epion_t3.core.common.bean.ExecuteFlow;
import com.epion_t3.core.common.bean.ExecuteScenario;
import com.epion_t3.core.common.bean.scenario.Flow;
import com.epion_t3.core.common.bean.scenario.HasChildrenFlow;
import com.epion_t3.core.common.context.Context;
import com.epion_t3.core.common.context.ExecuteContext;
import com.epion_t3.core.exception.SystemException;
import com.epion_t3.core.flow.bean.FlowResult;
import com.epion_t3.core.flow.resolver.impl.FlowRunnerResolverImpl;
import com.epion_t3.core.message.impl.CoreMessages;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

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
                    // ループ用終了ステータス
                    log.debug("force exit scenario.");
                    breakLoopFlg = true;
                    break;
                case CONTINUE:
                    // ループ用継続ステータス
                    log.debug("force exit scenario.");
                    continue;
                case WAIT:
                case RUNNING:
                    // 中間ステータスであるため、故障の可能性が高い
                    // 基本的に実行中間ステータスであるため終了時のステータスとして返却されることはない想定
                    log.debug("invalid flow status in ScenarioRunnerImpls. status : {}",
                            childFlowResult.getStatus().name());
                    throw new SystemException(CoreMessages.CORE_ERR_0066, childFlowResult.getStatus().name());
                case SUCCESS:
                case WARN:
                    log.debug("flow execute success or warn.");
                    break;
                default:
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

        }

    }

}
