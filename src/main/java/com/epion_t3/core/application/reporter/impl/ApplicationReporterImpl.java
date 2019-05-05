package com.epion_t3.core.application.reporter.impl;

import com.epion_t3.core.application.reporter.ThymeleafApplicationReporter;
import com.epion_t3.core.common.context.Context;
import com.epion_t3.core.common.context.ExecuteContext;
import com.epion_t3.core.common.type.StageType;
import com.epion_t3.core.exception.SystemException;
import com.epion_t3.core.message.impl.CoreMessages;
import com.epion_t3.core.common.util.ExecutionFileUtils;
import com.epion_t3.core.common.util.ThymeleafReportUtils;
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
     * @return シングルトンインスタンス
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
     * レポート出力.
     *
     * @param context        コンテキスト
     * @param executeContext 実行情報
     */
    @Override
    public void report(Context context, ExecuteContext executeContext) {

        // レポート出力ステージ
        executeContext.setStage(StageType.REPORT_ALL);

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
