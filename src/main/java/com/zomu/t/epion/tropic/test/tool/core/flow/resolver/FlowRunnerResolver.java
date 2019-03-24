package com.zomu.t.epion.tropic.test.tool.core.flow.resolver;

import com.zomu.t.epion.tropic.test.tool.core.flow.runner.FlowRunner;

/**
 * Flow実行クラス
 *
 * @author takashno
 */
public interface FlowRunnerResolver {

    FlowRunner getFlowRunner(String type);

}
