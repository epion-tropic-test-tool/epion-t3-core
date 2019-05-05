package com.epion_t3.core.command.reporter.impl;

import com.epion_t3.core.command.bean.CommandResult;
import com.epion_t3.core.common.context.Context;
import com.epion_t3.core.common.bean.ExecuteCommand;
import com.epion_t3.core.common.context.ExecuteContext;
import com.epion_t3.core.common.bean.ExecuteFlow;
import com.epion_t3.core.common.bean.ExecuteScenario;
import com.epion_t3.core.exception.SystemException;
import com.epion_t3.core.message.impl.CoreMessages;
import com.epion_t3.core.common.bean.scenario.Command;
import com.epion_t3.core.common.util.DateTimeUtils;
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
 * コマンドレポート出力処理の基底クラス.
 *
 * @param <COMMAND>
 * @author takashno
 */
@Slf4j
public abstract class AbstractThymeleafCommandReporter<
        COMMAND extends Command,
        COMMAND_RESULT extends CommandResult>
        implements ThymeleafCommandReporter<
        COMMAND, COMMAND_RESULT, ExecuteContext, ExecuteScenario, ExecuteFlow, ExecuteCommand> {

    /**
     * コマンドレポートを出力.
     *
     * @param command         コマンド
     * @param context         コンテキスト
     * @param executeContext  実行コンテキスト
     * @param executeScenario シナリオ実行情報
     * @param executeFlow     Flow実行情報
     * @param executeCommand  コマンド実行情報
     * @param t               エラー
     */
    @Override
    public void report(
            COMMAND command,
            COMMAND_RESULT commandResult,
            Context context,
            ExecuteContext executeContext,
            ExecuteScenario executeScenario,
            ExecuteFlow executeFlow,
            ExecuteCommand executeCommand,
            Throwable t) {

        TemplateEngine templateEngine = ThymeleafReportUtils.getInstance().createEngine();

        org.thymeleaf.context.Context thymeleafContext = new org.thymeleaf.context.Context();
        Map<String, Object> variable = new HashMap<>();

        // ユーティリティ設定
        ThymeleafReportUtils.setUtility(variable);

        try {

            // 変数の設定
            variable.put("command", command);
            variable.put("executeContext", executeContext);
            variable.put("executeScenario", executeScenario);
            variable.put("executeFlow", executeFlow);
            variable.put("executeCommand", executeCommand);
            variable.put("error", t);
            variable.put("hasError", t != null);

            // カスタム実装での変数設定
            setVariables(variable,
                    command,
                    commandResult,
                    executeContext,
                    executeScenario,
                    executeFlow,
                    executeCommand);

            // DateTimeUtilsを利用できるように設定
            variable.put("dateTimeUtils", DateTimeUtils.getInstance());

            // 変数設定
            thymeleafContext.setVariables(variable);

            // コマンドレポートパスを取得
            Path commandHtmlReportPath = ExecutionFileUtils.getCommandHtmlReportPath(executeScenario, executeCommand);

            // 親ディレクトリを作成
            Files.createDirectories(commandHtmlReportPath.getParent());

            // HTML変換＆出力
            Files.write(commandHtmlReportPath,
                    templateEngine.process(templatePath(),
                            thymeleafContext).getBytes(ThymeleafReportUtils.TEMPLATE_ENCODING));

            // カスタムレポート出力相対パス
            executeCommand.setCustomReportRelativePath("." +
                    commandHtmlReportPath.toString()
                            .replace(executeScenario.getResultPath().toString(), "")
                            .replaceAll("\\\\", "/"));

        } catch (IOException e) {

            log.debug("Error Occurred...", e);
            throw new SystemException(CoreMessages.CORE_ERR_1003);

        } catch (Exception e) {
            // TODO 暫定・・・
            log.error("ee", e);
        }
    }

}
