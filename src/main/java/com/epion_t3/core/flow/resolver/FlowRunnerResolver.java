package com.epion_t3.core.flow.resolver;

import com.epion_t3.core.flow.runner.FlowRunner;

/**
 * Flow実行クラス
 *
 * @author takashno
 */
public interface FlowRunnerResolver {

    FlowRunner getFlowRunner(String type);

}
