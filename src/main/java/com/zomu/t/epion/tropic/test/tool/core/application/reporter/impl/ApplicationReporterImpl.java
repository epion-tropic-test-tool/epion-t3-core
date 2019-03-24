package com.zomu.t.epion.tropic.test.tool.core.application.reporter.impl;

import com.zomu.t.epion.tropic.test.tool.core.application.reporter.ThymeleafApplicationReporter;
import com.zomu.t.epion.tropic.test.tool.core.context.Context;
import com.zomu.t.epion.tropic.test.tool.core.context.execute.ExecuteContext;
import com.zomu.t.epion.tropic.test.tool.core.exception.SystemException;
import com.zomu.t.epion.tropic.test.tool.core.message.impl.CoreMessages;
import com.zomu.t.epion.tropic.test.tool.core.util.ExecutionFileUtils;
import com.zomu.t.epion.tropic.test.tool.core.util.ThymeleafReportUtils;
import lombok.extern.slf4j.Slf4j;
import org.thymeleaf.TemplateEngine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * アプリケーションレポート出力処理.
 *
 * @author takashno
 */
@Slf4j
public class ApplicationReporterImpl implements ThymeleafApplicationReporter<ExecuteContext> {

    /**
     * シングルトンインスタンス.
     */
    private static final ApplicationReporterImpl instance = new ApplicationReporterImpl();

    /**
     * インスタンスを取得する.
     *
     * @return
     */
    public static ApplicationReporterImpl getInstance() {
        return instance;
    }

    /**
     * プライベートコンストラクタ.
     */
    private ApplicationReporterImpl() {
        // Do Nothing...
    }

    /**
     * @param context        コンテキスト
     * @param executeContext 実行情報
     * @param t              エラー
     */
    @Override
    public void report(Context context, ExecuteContext executeContext, Throwable t) {

        TemplateEngine templateEngine = ThymeleafReportUtils.getInstance().createEngine();

        org.thymeleaf.context.Context thymeleafContext = new org.thymeleaf.context.Context();
        Map<String, Object> variable = new HashMap<>();

        // ユーティリティ設定
        ThymeleafReportUtils.setUtility(variable);

        try {
            variable.put("context", context);
            variable.put("executeContext", executeContext);

            // 変数設定
            thymeleafContext.setVariables(variable);

            Path applicationReportPath = ExecutionFileUtils.getAllReportPath(executeContext);

            // HTML変換＆出力
            Files.write(applicationReportPath,
                    templateEngine.process(
                            templatePath(), thymeleafContext).getBytes(ThymeleafReportUtils.TEMPLATE_ENCODING));

        } catch (IOException e) {

            log.debug("Error Occurred...", e);

            throw new SystemException(CoreMessages.CORE_ERR_1001);

        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String templatePath() {
        return "report";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setVariables(Map<String, Object> variable) {
        // Do Nothing...
    }

}
