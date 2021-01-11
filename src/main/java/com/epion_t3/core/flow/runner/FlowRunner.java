/* Copyright (c) 2017-2019 Nozomu Takashima. */
package com.epion_t3.core.flow.runner;

import com.epion_t3.core.common.bean.ExecuteScenario;
import com.epion_t3.core.common.bean.scenario.Flow;
import com.epion_t3.core.common.context.Context;
import com.epion_t3.core.common.context.ExecuteContext;
import com.epion_t3.core.flow.bean.FlowResult;

/**
 * フロー実行処理インタフェース.
 *
 * @param <FLOW>
 * @author takashno
 */
public interface FlowRunner<FLOW extends Flow> {

    /**
     * Flowを実行します.
     *
     * @param context         コンテキスト
     * @param executeContext  実行コンテキスト
     * @param executeScenario 実行シナリオ
     * @param flow            Flow
     */
    FlowResult execute(final Context context, final ExecuteContext executeContext,
                       final ExecuteScenario executeScenario, final FLOW flow);

}
