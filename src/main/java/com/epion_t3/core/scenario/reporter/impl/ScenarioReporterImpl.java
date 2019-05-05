package com.epion_t3.core.scenario.reporter.impl;

import com.epion_t3.core.common.annotation.OriginalProcessField;
import com.epion_t3.core.common.context.Context;
import com.epion_t3.core.common.bean.ExecuteCommand;
import com.epion_t3.core.common.context.ExecuteContext;
import com.epion_t3.core.common.bean.ExecuteFlow;
import com.epion_t3.core.common.bean.ExecuteScenario;
import com.epion_t3.core.common.type.StageType;
import com.epion_t3.core.exception.SystemException;
import com.epion_t3.core.message.impl.CoreMessages;
import com.epion_t3.core.scenario.reporter.ThymeleafScenarioReporter;
import com.epion_t3.core.common.util.ExecutionFileUtils;
import com.epion_t3.core.common.util.ThymeleafReportUtils;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import org.apache.commons.io.IOUtils;
import org.apache.commons.text.WordUtils;
import org.thymeleaf.TemplateEngine;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * シナリオレポート出力処理.
 *
 * @author takashno
 */
@Slf4j
public final class ScenarioReporterImpl implements ThymeleafScenarioReporter<ExecuteContext, ExecuteScenario> {

    /**
     * シングルトンインスタンス.
     */
    private static final ScenarioReporterImpl instance = new ScenarioReporterImpl();

    /**
     * インスタンスを取得する.
     *
     * @return シングルトンインスタンス
     */
    public static ScenarioReporterImpl getInstance() {
        return instance;
    }

    /**
     * プライベートコンストラクタ.
     */
    private ScenarioReporterImpl() {
        // Do Nothing...
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String templatePath() {
        return "scenario";
    }

    /**
     * {@inheritDoc}
     *
     * @param variable
     */
    @Override
    public void setVariables(Map variable) {
        // Do Nothing...
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void report(
            Context context,
            ExecuteContext executeContext,
            ExecuteScenario executeScenario,
            Throwable t) {

        // シナリオレポート出力ステージ
        executeContext.setStage(StageType.REPORT_SCENARIO);

        TemplateEngine templateEngine = ThymeleafReportUtils.getInstance().createEngine();

        org.thymeleaf.context.Context thymeleafContext = new org.thymeleaf.context.Context();
        Map<String, Object> variable = new HashMap<>();

        // ユーティリティ設定
        ThymeleafReportUtils.setUtility(variable);

        // 変数設定
        setVariables(variable);

        try {

            for (ExecuteFlow executeFlow : executeScenario.getFlows()) {
                for (ExecuteCommand executeCommand : executeFlow.getCommands()) {
                    for (Field field : executeCommand.getCommand().getClass().getDeclaredFields()) {
                        if (field.getDeclaredAnnotation(OriginalProcessField.class) == null) {
                            if (executeCommand.getExtensionProcessFields() == null) {
                                executeCommand.setExtensionProcessFields(new HashMap<>());
                            }
                            try {
                                // Getterメソッドを参照
                                Method getterMethod = executeCommand.getCommand().getClass().getDeclaredMethod(
                                        "get" + WordUtils.capitalize(field.getName()), null);
                                // 値抽出
                                Object value = getterMethod.invoke(executeCommand.getCommand());
                                executeCommand.getExtensionProcessFields().put(field.getName(), value);
                            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                                log.debug("error occurred...", e);
                                // Ignore
                            }
                        }
                    }
                }
            }

            // 変数の設定
            // variable.put("activity", generateSvg(executeScenario));
            variable.put("executeContext", executeContext);
            variable.put("executeScenario", executeScenario);
            variable.put("error", t);
            variable.put("hasError", t != null);

            // 変数設定
            thymeleafContext.setVariables(variable);

            Path scenarioReportPath = ExecutionFileUtils.getScenarioReportPath(executeContext, executeScenario);

            // HTML変換＆出力
            Files.write(scenarioReportPath,
                    templateEngine.process(
                            templatePath(), thymeleafContext).getBytes(ThymeleafReportUtils.TEMPLATE_ENCODING));

        } catch (IOException e) {

            log.debug("Error Occurred...", e);

            throw new SystemException(CoreMessages.CORE_ERR_1002);
        }

    }


    /**
     * 実行Flowのアクティビティ図のSVGを出力する.
     *
     * @param executeScenario シナリオ実行情報
     * @return PlantUMLのアクティビティ図の文字列表現
     */
    private String generateSvg(ExecuteScenario executeScenario) {

        StringBuilder activity = new StringBuilder();
        boolean first = true;
        for (ExecuteFlow executeFlow : executeScenario.getFlows()) {
            if (first) {
                activity.append("(*)-->");
                activity.append(executeFlow.getFlow().getId());
                first = false;
            } else {
                activity.append("-->");
                activity.append(executeFlow.getFlow().getId());
            }
            switch (executeFlow.getStatus()) {
                case WAIT:
                    activity.append("<<WAIT>>");
                    break;
                case SKIP:
                    activity.append("<<SKIP>>");
                    break;
                case SUCCESS:
                    activity.append("<<SUCCESS>>");
                    break;
                case ASSERT_ERROR:
                    activity.append("<<ASSERT_ERROR>>");
                    break;
                case ERROR:
                    activity.append("<<ERROR>>");
                    break;
                case WARN:
                    activity.append("<<WARN>>");
                    break;
            }
            activity.append("\n");
        }
        activity.append("-->(*)\n");

        List<String> templates = null;
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("activity/activity_template.puml")) {
            templates = IOUtils.readLines(is, Charset.forName("UTF-8"));
        } catch (IOException e) {
            throw new SystemException(e);
        }

        String activityString = activity.toString();
        StringBuilder result = new StringBuilder();
        for (String row : templates) {
            result.append(row.replace("'$ACTIVITY$", activityString));
            result.append("\n");
        }

        String plantUmlSrc = result.toString();
        SourceStringReader reader = new SourceStringReader(plantUmlSrc);
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            reader.generateImage(os, new FileFormatOption(FileFormat.SVG));
            return new String(os.toByteArray(), Charset.forName("UTF-8"));
        } catch (IOException e) {
            throw new SystemException(e);
        }
    }
}
