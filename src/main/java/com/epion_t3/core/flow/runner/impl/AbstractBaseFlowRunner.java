package com.epion_t3.core.flow.runner.impl;

import com.epion_t3.core.common.bean.ExecuteFlow;
import com.epion_t3.core.common.bean.ExecuteScenario;
import com.epion_t3.core.common.bean.scenario.Flow;
import com.epion_t3.core.common.context.Context;
import com.epion_t3.core.common.context.ExecuteContext;
import com.epion_t3.core.common.type.FlowScopeVariables;
import com.epion_t3.core.common.type.FlowStatus;
import com.epion_t3.core.common.util.BindUtils;
import com.epion_t3.core.flow.runner.FlowRunner;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * FlowRunnerの基本機能を提供する抽象クラス.
 *
 * @since 0.0.4
 * @param <FLOW>
 */
@Slf4j
public abstract class AbstractBaseFlowRunner<FLOW extends Flow> implements FlowRunner<FLOW> {

    /**
     * ロギングマーカー.
     *
     * @since 0.0.4
     */
    protected Marker collectLoggingMarker;

    /**
     * ExecuteFlowを作成します.
     *
     * @return ExecuteFlow
     * @since 0.0.4
     */
    protected ExecuteFlow createExecuteFlow() {
        var executeFlow = new ExecuteFlow();
        collectLoggingMarker = MarkerFactory.getMarker(executeFlow.getExecuteId().toString());
        return executeFlow;
    }

    /**
     * 収集対象のロギングマーカーを取得します.
     *
     * @return ロギングマーカー
     * @since 0.0.4
     */
    protected Marker collectLoggingMarker() {
        return this.collectLoggingMarker;
    }

    /**
     * Flowに対して、変数をバインドします.
     *
     * @param context         コンテキスト
     * @param executeScenario シナリオ実行情報
     * @param executeFlow     Flow実行情報
     * @param flow            Flow
     */
    protected void bind(final Context context, final ExecuteContext executeContext,
                        final ExecuteScenario executeScenario, final ExecuteFlow executeFlow, final Flow flow) {

        BindUtils.getInstance()
                .bind(flow, executeScenario.getProfileConstants(), executeContext.getGlobalVariables(),
                        executeScenario.getScenarioVariables(), executeFlow.getFlowVariables());
    }

    /**
     * シナリオスコープの変数を設定する.
     * プロセス実行時に指定を行うべきシナリオスコープ変数の設定処理.
     *
     * @param context         コンテキスト
     * @param executeScenario シナリオ実行情報
     * @param executeFlow     FLOW実行情報
     */
    protected void settingFlowVariables(final Context context, final ExecuteContext ExecuteContext,
                                        final ExecuteScenario executeScenario, final ExecuteFlow executeFlow) {

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
    protected void cleanFlowVariables(final Context context, final ExecuteContext executeContext,
                                      final ExecuteScenario executeScenario, final ExecuteFlow executeFlow) {

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
