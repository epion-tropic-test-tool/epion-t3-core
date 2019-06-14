package com.epion_t3.core.flow.runner.impl;

import com.epion_t3.core.common.bean.scenario.Flow;
import com.epion_t3.core.common.type.FlowResultStatus;
import com.epion_t3.core.exception.SystemException;
import com.epion_t3.core.flow.model.FlowResult;
import com.epion_t3.core.flow.model.WhileFlow;
import com.epion_t3.core.flow.resolver.impl.FlowRunnerResolverImpl;
import com.epion_t3.core.flow.runner.FlowRunner;
import com.epion_t3.core.message.impl.CoreMessages;
import org.slf4j.Logger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


public class WhileFlowRunner extends AbstractFlowRunner<WhileFlow> {
    @Override
    protected FlowResult execute(WhileFlow flow, Logger logger) {

        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("JavaScript");

        engine.put("global", executeContext.getGlobalVariables());
        engine.put("scenario", executeScenario.getScenarioVariables());
        engine.put("flow", executeFlow.getFlowVariables());

        try {

            // Flow実行開始時間を設定
            LocalDateTime start = LocalDateTime.now();

            // 初回ループのための判定
            Object scriptResult = engine.eval(flow.getCondition());
            if (scriptResult != null && Boolean.class.isAssignableFrom(scriptResult.getClass())) {
                Boolean scriptResultBoolean = (Boolean) scriptResult;
                if (scriptResultBoolean) {

                    // Whileループ
                    while (scriptResultBoolean) {

                        for (Flow child : flow.getChildren()) {
                            FlowRunner flowRunner = FlowRunnerResolverImpl.getInstance().getFlowRunner(child.getType());
                            flowRunner.execute(context, executeContext, executeScenario, executeFlow, child, logger);
                        }

                        // 次のループ処理を行うかの判定
                        scriptResult = engine.eval(flow.getCondition());
                        if (scriptResult != null && Boolean.class.isAssignableFrom(scriptResult.getClass())) {
                            scriptResultBoolean = (Boolean) scriptResult;
                        }

                        // タイムアウト判定
                        if (flow.getTimeout() != null) {
                            LocalDateTime end = LocalDateTime.now();
                            Duration duration = Duration.between(start, end);
                            long durationMilliseconds = duration.get(ChronoUnit.MILLIS);
                            if (durationMilliseconds < flow.getTimeout()) {
                                FlowResult flowResult = new FlowResult();
                                flowResult.setStatus(FlowResultStatus.CHOICE);
                                if (flow.getContinueFlow()) {
                                    flowResult.setStatus(FlowResultStatus.NEXT);
                                } else {
                                    throw new SystemException(CoreMessages.CORE_WRN_0004, flow.getTimeout());
                                }
                                return flowResult;
                            }
                        }
                    }
                }
            }
            FlowResult flowResult = new FlowResult();
            flowResult.setStatus(FlowResultStatus.NEXT);
            return flowResult;

        } catch (ScriptException e) {
            throw new SystemException(e);
        }
    }
}

