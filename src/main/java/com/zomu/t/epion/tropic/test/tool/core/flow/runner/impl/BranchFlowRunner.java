package com.zomu.t.epion.tropic.test.tool.core.flow.runner.impl;

import com.zomu.t.epion.tropic.test.tool.core.context.Context;
import com.zomu.t.epion.tropic.test.tool.core.context.execute.ExecuteContext;
import com.zomu.t.epion.tropic.test.tool.core.context.execute.ExecuteFlow;
import com.zomu.t.epion.tropic.test.tool.core.context.execute.ExecuteScenario;
import com.zomu.t.epion.tropic.test.tool.core.exception.SystemException;
import com.zomu.t.epion.tropic.test.tool.core.flow.model.BranchFlow;
import com.zomu.t.epion.tropic.test.tool.core.flow.model.FlowResult;
import com.zomu.t.epion.tropic.test.tool.core.message.impl.CoreMessages;
import com.zomu.t.epion.tropic.test.tool.core.type.FlowResultStatus;
import org.slf4j.Logger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * 条件分岐を判断するためのFlowRunner.
 * <p>
 * シナリオに記載された任意のJavaScript式を評価し、
 * その結果をもとに、どのFlowへ実行するべきかを判定する.
 * </p>
 *
 * @author takashno
 */
public class BranchFlowRunner extends AbstractFlowRunner<
        ExecuteContext,
        ExecuteScenario,
        ExecuteFlow,
        BranchFlow> {

    @Override
    protected FlowResult execute(
            final Context context,
            final ExecuteContext executeContext,
            final ExecuteScenario executeScenario,
            final ExecuteFlow executeFlow,
            final BranchFlow flow,
            final Logger logger) {


        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("JavaScript");

        engine.put("global", executeContext.getGlobalVariables());
        engine.put("scenario", executeScenario.getScenarioVariables());
        engine.put("flow", executeFlow.getFlowVariables());

        try {
            Object scriptResult = engine.eval(flow.getCondition());
            if (scriptResult != null && Boolean.class.isAssignableFrom(scriptResult.getClass())) {
                FlowResult flowResult = new FlowResult();
                flowResult.setStatus(FlowResultStatus.CHOICE);
                flowResult.setChoiceId((Boolean) scriptResult ? flow.getTrueRef() : flow.getFalseRef());
                return flowResult;
            } else {
                // TODO:Error
                throw new SystemException(CoreMessages.CORE_ERR_0001);
            }
        } catch (ScriptException e) {
            throw new SystemException(e);
        }
    }
}
