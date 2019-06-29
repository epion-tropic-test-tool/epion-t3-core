package com.epion_t3.core.flow.runner.impl;

import com.epion_t3.core.common.context.Context;
import com.epion_t3.core.common.context.ExecuteContext;
import com.epion_t3.core.common.bean.ExecuteFlow;
import com.epion_t3.core.common.bean.ExecuteScenario;
import com.epion_t3.core.exception.SystemException;
import com.epion_t3.core.flow.model.FlowResult;
import com.epion_t3.core.flow.model.ReadTextFileIterateFlow;
import com.epion_t3.core.flow.resolver.impl.FlowRunnerResolverImpl;
import com.epion_t3.core.flow.runner.FlowRunner;
import com.epion_t3.core.common.bean.scenario.Flow;
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
