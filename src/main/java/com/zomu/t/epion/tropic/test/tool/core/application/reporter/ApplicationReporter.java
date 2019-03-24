package com.zomu.t.epion.tropic.test.tool.core.application.reporter;

import com.zomu.t.epion.tropic.test.tool.core.context.Context;
import com.zomu.t.epion.tropic.test.tool.core.context.execute.ExecuteContext;

/**
 * アプリケーションレポート出力インタフェース.
 *
 * @param <EXECUTE_CONTEXT>
 * @author takashno
 */
public interface ApplicationReporter<EXECUTE_CONTEXT extends ExecuteContext> {

    /**
     * アプリケーションレポート出力.
     *
     * @param context        コンテキスト
     * @param executeContext 実行情報
     * @param t              エラー
     */
    void report(Context context,
                EXECUTE_CONTEXT executeContext,
                Throwable t);
}
