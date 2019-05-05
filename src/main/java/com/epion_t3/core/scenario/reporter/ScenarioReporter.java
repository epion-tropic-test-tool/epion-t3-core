package com.epion_t3.core.scenario.reporter;

import com.epion_t3.core.common.context.Context;
import com.epion_t3.core.common.context.ExecuteContext;
import com.epion_t3.core.common.bean.ExecuteScenario;

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
