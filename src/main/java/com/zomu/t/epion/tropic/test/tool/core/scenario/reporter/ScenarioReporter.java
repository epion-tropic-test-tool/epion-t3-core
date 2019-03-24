package com.zomu.t.epion.tropic.test.tool.core.scenario.reporter;

import com.zomu.t.epion.tropic.test.tool.core.context.Context;
import com.zomu.t.epion.tropic.test.tool.core.context.execute.ExecuteContext;
import com.zomu.t.epion.tropic.test.tool.core.context.execute.ExecuteScenario;

/**
 * シナリオレポート出力インタフェース.
 *
 * @param <EXECUTE_CONTEXT>
 * @param <EXECUTE_SCENARIO>
 * @author takashno
 */
public interface ScenarioReporter<EXECUTE_CONTEXT extends ExecuteContext, EXECUTE_SCENARIO extends ExecuteScenario> {

    /**
     * シナリオレポート出力.
     *
     * @param context         コンテキスト
     * @param executeContext  実行情報
     * @param executeScenario シナリオ実行情報
     * @param t               エラー
     */
    void report(Context context,
                EXECUTE_CONTEXT executeContext,
                EXECUTE_SCENARIO executeScenario,
                Throwable t);


}
