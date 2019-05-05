package com.epion_t3.core.common.util;

import com.epion_t3.core.common.context.Context;
import com.epion_t3.core.common.bean.ExecuteCommand;
import com.epion_t3.core.common.context.ExecuteContext;
import com.epion_t3.core.common.bean.ExecuteScenario;
import com.epion_t3.core.common.type.ScenarioScopeVariables;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;

/**
 * @author takashno
 */
public final class ExecutionFileUtils {

    /**
     * 結果ディレクトリのフォーマット.
     */
    public static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    /**
     * エビデンス格納ディレクトリ名.
     */
    public static final String EVIDENCE_DIR_NAME = "evidence";

    /**
     * プライベートコンストラクタ.
     */
    private ExecutionFileUtils() {
        // Do Nothing...
    }

    /**
     * @param context
     */
    public static void createResultDirectory(final Context context, final ExecuteContext executeContext) {

        Path resultRootPath = null;

        if (StringUtils.isNotEmpty(context.getOption().getResultRootPath())) {
            resultRootPath = Paths.get(context.getOption().getResultRootPath());
        } else {

            resultRootPath = Paths.get(
                    SystemUtils.getUserDir()
                            + File.separator
                            + "result"
                            + File.separator
                            + DTF.format(executeContext.getStart()));
        }

        try {
            Files.createDirectories(resultRootPath);
            executeContext.setResultRootPath(resultRootPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param context
     */
    public static void createScenarioResultDirectory(
            final Context context,
            final ExecuteContext executeContext,
            final ExecuteScenario executeScenario) {

        try {
            Path resultPath = Paths.get(
                    executeContext.getResultRootPath().toFile().getPath()
                            + File.separator
                            + executeScenario.getInfo().getId()
                            + "_"
                            + DTF.format(executeScenario.getStart()));

            Files.createDirectories(resultPath);
            executeScenario.setResultPath(resultPath);

            Path evidencePath = Paths.get(
                    executeScenario.getResultPath().toFile().getPath()
                            + File.separator
                            + EVIDENCE_DIR_NAME);

            Files.createDirectories(evidencePath);
            executeScenario.setEvidencePath(evidencePath);
            executeScenario.getScenarioVariables().put(ScenarioScopeVariables.EVIDENCE_DIR.getName(), evidencePath.toString());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Path getAllReportPath(final ExecuteContext executeContext) {
        return Paths.get(executeContext.getResultRootPath() + File.separator + "report.html");
    }

    public static Path getAllReportYamlPath(final ExecuteContext executeContext) {
        return Paths.get(executeContext.getResultRootPath() + File.separator + "report.yaml");
    }

    public static Path getScenarioReportPath(final ExecuteContext executeContext, final ExecuteScenario scenario) {
        return Paths.get(scenario.getResultPath().toString() + File.separator + "scenario_report.html");
    }

    /**
     * コマンドレポートの出力パスを取得.
     *
     * @param scenario
     * @param executeCommand
     * @param extention
     * @return
     */
    public static Path getCommandReportPath(final ExecuteScenario scenario,
                                            final ExecuteCommand executeCommand,
                                            final String extention) {
        return Paths.get(scenario.getResultPath().toString()
                + File.separator
                + "details"
                + File.separator
                + String.format("command_%s.%s", executeCommand.getExecuteId().toString(), extention));
    }

    /**
     * コマンドHTMLレポートの出力パスを取得.
     *
     * @param scenario
     * @param executeCommand
     * @return
     */
    public static Path getCommandHtmlReportPath(final ExecuteScenario scenario,
                                                final ExecuteCommand executeCommand) {
        return getCommandReportPath(scenario, executeCommand, "html");
    }


    /**
     * FullコマンドIDを引数に属するシナリオが配置しているパスを取得.
     *
     * @param context
     * @param executeContext
     * @param fqcn
     * @return
     */
    public static Path getBelongScenarioDirectory(final Context context,
                                                  final ExecuteContext executeContext,
                                                  final String fqcn) {
        String scenarioId = IDUtils.getInstance().extractBelongScenarioIdFromFqcn(fqcn);
        if (!StringUtils.isNotEmpty(scenarioId)) {
            Path scenarioPath = context.getOriginal().getScenarioPlacePaths().get(scenarioId);
            return scenarioPath;
        }
        return null;
    }


}
