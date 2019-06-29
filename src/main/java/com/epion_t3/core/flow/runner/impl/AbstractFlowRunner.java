package com.epion_t3.core.flow.runner.impl;

import com.epion_t3.core.common.context.Context;
import com.epion_t3.core.common.bean.ExecuteCommand;
import com.epion_t3.core.common.context.ExecuteContext;
import com.epion_t3.core.common.bean.ExecuteFlow;
import com.epion_t3.core.common.bean.ExecuteScenario;
import com.epion_t3.core.flow.model.FlowResult;
import com.epion_t3.core.flow.runner.FlowRunner;
import com.epion_t3.core.flow.logging.bean.FlowLog;
import com.epion_t3.core.flow.logging.holder.FlowLoggingHolder;
import com.epion_t3.core.common.bean.scenario.Flow;
import com.epion_t3.core.common.type.FlowScopeVariables;
import com.epion_t3.core.common.type.FlowStatus;
import com.epion_t3.core.common.util.BindUtils;
import com.epion_t3.core.common.util.ErrorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/**
 * 全てのFlowの基底クラス.
 *
 * @param <EXECUTE_CONTEXT>
 * @param <EXECUTE_SCENARIO>
 * @param <EXECUTE_FLOW>
 * @param <FLOW>
 */
@Slf4j
public abstract class AbstractFlowRunner<
        EXECUTE_CONTEXT extends ExecuteContext,
        EXECUTE_SCENARIO extends ExecuteScenario,
        EXECUTE_FLOW extends ExecuteFlow,
        FLOW extends Flow>
        implements FlowRunner<Context, EXECUTE_CONTEXT, EXECUTE_SCENARIO, FLOW> {

    /**
     * {@inheritDoc}
     */
    @Override
    public FlowResult execute(
            Context context,
            EXECUTE_CONTEXT executeContext,
            EXECUTE_SCENARIO executeScenario,
            FLOW flow,
            Logger logger) {

        // process実行情報を作成
        EXECUTE_FLOW executeFlow = getExecuteFlowInstance();
        executeScenario.getFlows().add(executeFlow);
        executeFlow.setFlow(flow);

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
            outputStartFlowLog(
                    context,
                    executeScenario,
                    executeFlow);

            // Flowスコープ変数の設定
            settingFlowVariables(
                    context,
                    executeContext,
                    executeScenario,
                    executeFlow);

            // バインド
            bind(
                    context,
                    executeContext,
                    executeScenario,
                    executeFlow,
                    flow);

            // 実行
            flowResult = execute(
                    context,
                    executeContext,
                    executeScenario,
                    executeFlow,
                    flow,
                    logger);


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
            onError(context, executeContext, executeScenario, executeFlow,
                    flow, t, logger);

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
            executeFlow.setDuration(Duration.between(
                    executeFlow.getStart(), executeFlow.getEnd()));

            // プロセスのログを収集
            List<FlowLog> flowLogs = SerializationUtils.clone(FlowLoggingHolder.get());
            executeFlow.setFlowLogs(flowLogs);

            // プロセスのログは収集し終えたらクリアする（ThreadLocalにて保持）
            FlowLoggingHolder.clear();

            // プロセス終了ログ出力
            outputEndFlowLog(context, executeScenario, executeFlow);

            // エラー処理
            onFinally(context, executeContext, executeScenario, executeFlow, flow, logger);

        }

        return flowResult;
    }

    /**
     * @return
     */
    private EXECUTE_FLOW getExecuteFlowInstance() {
        try {
            Class<?> clazz = this.getClass();
            Type type = clazz.getGenericSuperclass();
            ParameterizedType pt = (ParameterizedType) type;
            Type[] actualTypeArguments = pt.getActualTypeArguments();
            Class<?> entityClass = (Class<?>) actualTypeArguments[2];
            return (EXECUTE_FLOW) entityClass.newInstance();
        } catch (ReflectiveOperationException e) {
            // TODO:ErrorProcess
            throw new RuntimeException(e);
        }
    }


    /**
     * Flowに対して、変数をバインドする.
     *
     * @param context         コンテキスト
     * @param executeScenario シナリオ実行情報
     * @param executeFlow     Flow実行情報
     * @param flow            Flow
     */
    private void bind(final Context context,
                      final EXECUTE_CONTEXT executeContext,
                      final EXECUTE_SCENARIO executeScenario,
                      final EXECUTE_FLOW executeFlow,
                      final FLOW flow) {

        BindUtils.getInstance().bind(
                flow,
                executeScenario.getProfileConstants(),
                executeContext.getGlobalVariables(),
                executeScenario.getScenarioVariables(),
                executeFlow.getFlowVariables());
    }


    /**
     * @param context         コンテキスト
     * @param executeScenario シナリオ実行情報
     * @param executeFlow     Flow実行情報
     * @param flow            Flow
     */
    protected abstract FlowResult execute(
            Context context,
            EXECUTE_CONTEXT execute_context,
            EXECUTE_SCENARIO executeScenario,
            EXECUTE_FLOW executeFlow,
            FLOW flow,
            Logger logger);


    /**
     * エラー処理を行う.
     * この処理は、Flowの処理結果が失敗の場合に実行される.
     *
     * @param context         コンテキスト
     * @param executeScenario シナリオ実行情報
     * @param executeFlow     Flow実行情報
     * @param flow            Flow
     * @param t               例外
     */
    protected void onError(
            Context context,
            EXECUTE_CONTEXT execute_context,
            EXECUTE_SCENARIO executeScenario,
            EXECUTE_FLOW executeFlow,
            FLOW flow,
            Throwable t,
            Logger logger) {
        // 必要に応じてオーバーライド実装すること.
    }

    /**
     * 終了処理を行う.
     * この処理は、Flowの処理結果が成功・失敗に関わらず実行される.
     *
     * @param context         コンテキスト
     * @param executeScenario シナリオ実行情報
     * @param executeFlow     Flow実行情報
     * @param flow            Flow
     */
    protected void onFinally(
            final Context context,
            final EXECUTE_CONTEXT execute_context,
            final EXECUTE_SCENARIO executeScenario,
            final EXECUTE_FLOW executeFlow,
            final FLOW flow,
            final Logger logger) {
        // 必要に応じてオーバーライド実装すること.
    }

    /**
     * シナリオスコープの変数を設定する.
     * プロセス実行時に指定を行うべきシナリオスコープ変数の設定処理.
     *
     * @param context         コンテキスト
     * @param executeScenario シナリオ実行情報
     * @param executeFlow     FLOW実行情報
     */
    private void settingFlowVariables(final Context context,
                                      final EXECUTE_CONTEXT execute_context,
                                      final EXECUTE_SCENARIO executeScenario,
                                      final ExecuteFlow executeFlow) {

        // 現在の処理Flow
        executeFlow.getFlowVariables().put(
                FlowScopeVariables.CURRENT_FLOW.getName(),
                executeFlow.getFlow().getId());

        // 現在の処理FLOWの実行ID
        executeFlow.getFlowVariables().put(
                FlowScopeVariables.CURRENT_FLOW_EXECUTE_ID.getName(),
                executeFlow.getExecuteId());
    }

    /**
     * シナリオスコープの変数を掃除する.
     * プロセス実行時にのみ指定すべきシナリオスコープの変数を確実に除去するための処理.
     *
     * @param context         コンテキスト
     * @param executeScenario シナリオ実行情報
     * @param executeFlow     FLOW実行情報
     */
    private void cleanFlowVariables(final Context context,
                                    final EXECUTE_CONTEXT executeContext,
                                    final EXECUTE_SCENARIO executeScenario,
                                    final ExecuteFlow executeFlow) {

        // 現在の処理Flow
        executeFlow.getFlowVariables().remove(
                FlowScopeVariables.CURRENT_FLOW.getName());

        // 現在の処理FLOWの実行ID
        executeFlow.getFlowVariables().remove(
                FlowScopeVariables.CURRENT_FLOW_EXECUTE_ID.getName());
    }


    /**
     * Flow開始ログ出力.
     *
     * @param context         コンテキスト
     * @param executeScenario 実行シナリオ
     * @param executeFlow     実行Flow
     */
    protected void outputStartFlowLog(Context context, ExecuteScenario executeScenario, ExecuteFlow executeFlow) {
        //log.info("--------------------------------------------------------------------------------------");
        log.info("■ Start Flow    ■ Scenario ID : {}, Flow ID : {}", executeScenario.getInfo().getId(), executeFlow.getFlow().getId());
        //log.info("Scenario ID         : {}", executeScenario.getInfo().getId());
        //log.info("Execute Flow ID  : {}", executeFlow.getExecuteId());
        //log.info("--------------------------------------------------------------------------------------");
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
            // log.error("--------------------------------------------------------------------------------------");
            log.info("■ End Flow      ■ Scenario ID : {}, Flow ID : {}, Flow Status : {}", executeScenario.getInfo().getId(), executeFlow.getFlow().getId(), executeFlow.getStatus().name());
            // log.info("Scenario ID         : {}", executeScenario.getInfo().getId());
            // log.info("Execute Flow ID  : {}", executeFlow.getExecuteId());
            // log.info("Flow Status      : {}", executeFlow.getStatus().name());
            // log.info("--------------------------------------------------------------------------------------");
        } else if (executeFlow.getStatus() == FlowStatus.ERROR) {
            // log.error("--------------------------------------------------------------------------------------");
            log.error("■ End Flow      ■ Scenario ID : {}, Flow ID : {}, Flow Status : {}", executeScenario.getInfo().getId(), executeFlow.getFlow().getId(), executeFlow.getStatus().name());
            // log.error("Scenario ID         : {}", executeScenario.getInfo().getId());
            // log.error("Execute Flow ID  : {}", executeFlow.getExecuteId());
            // log.error("Flow Status      : {}", executeFlow.getStatus().name());
            // log.error("--------------------------------------------------------------------------------------");
        } else {
            // log.warn("--------------------------------------------------------------------------------------");
            log.warn("■ End Flow      ■ Scenario ID : {}, Flow ID : {}, Flow Status : {}", executeScenario.getInfo().getId(), executeFlow.getFlow().getId(), executeFlow.getStatus().name());
            // log.warn("Scenario ID         : {}", executeScenario.getInfo().getId());
            // log.warn("Execute Flow ID  : {}", executeFlow.getExecuteId());
            // log.warn("Flow Status      : {}", executeFlow.getStatus().name());
            // log.warn("--------------------------------------------------------------------------------------");
        }

    }

}
