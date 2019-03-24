package com.zomu.t.epion.tropic.test.tool.core.flow.runner.impl;

import com.zomu.t.epion.tropic.test.tool.core.context.Context;
import com.zomu.t.epion.tropic.test.tool.core.context.execute.ExecuteCommand;
import com.zomu.t.epion.tropic.test.tool.core.context.execute.ExecuteContext;
import com.zomu.t.epion.tropic.test.tool.core.context.execute.ExecuteFlow;
import com.zomu.t.epion.tropic.test.tool.core.context.execute.ExecuteScenario;
import com.zomu.t.epion.tropic.test.tool.core.exception.SystemException;
import com.zomu.t.epion.tropic.test.tool.core.flow.model.CommandExecuteFlow;
import com.zomu.t.epion.tropic.test.tool.core.flow.model.FlowResult;
import com.zomu.t.epion.tropic.test.tool.core.flow.model.ReadTextFileIterateFlow;
import com.zomu.t.epion.tropic.test.tool.core.flow.resolver.impl.FlowRunnerResolverImpl;
import com.zomu.t.epion.tropic.test.tool.core.flow.runner.FlowRunner;
import com.zomu.t.epion.tropic.test.tool.core.model.scenario.Flow;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * ファイルを読み込んで1行毎にループ処理を行う.
 *
 * @author takashno
 */
public class ReadFileIterateFlowRunner
        extends AbstractFlowRunner<
        ExecuteContext,
        ExecuteScenario,
        ExecuteFlow,
        ReadTextFileIterateFlow> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected FlowResult execute(
            final Context context,
            final ExecuteContext executeContext,
            final ExecuteScenario executeScenario,
            final ExecuteFlow executeFlow,
            final ReadTextFileIterateFlow flow,
            final Logger logger) {


        Path target = Paths.get(flow.getTarget());

        if (!Files.exists(target)) {
            throw new SystemException("not found target File...");
        }

        List<String> contents = null;

        try {
            contents = Files.readAllLines(target, Charset.forName(flow.getEncoding()));
        } catch (IOException e) {

            // 解析用
            logger.debug("error occurred...", e);

            throw new SystemException(e);

        }

        for (String row : contents) {

            String[] cols = row.split(",");

            for (int i = 0; i < cols.length; i++) {
                executeFlow.getFlowVariables().put("row_col_" + (i + 1), cols[i]);
            }

            for (Flow child : flow.getChildren()) {

                FlowRunner flowRunner = FlowRunnerResolverImpl.getInstance().getFlowRunner(child.getType());
                flowRunner.execute(context,
                        executeContext,
                        executeScenario,
                        child,
                        logger);
            }
        }

        return FlowResult.getDefault();

    }
}
