package com.epion_t3.core.command.runner.impl;

import com.epion_t3.core.command.bean.CommandResult;
import com.epion_t3.core.command.reporter.CommandReporter;
import com.epion_t3.core.command.reporter.impl.NoneCommandReporter;
import com.epion_t3.core.command.runner.CommandRunner;
import com.epion_t3.core.common.context.Context;
import com.epion_t3.core.common.bean.EvidenceInfo;
import com.epion_t3.core.common.bean.ExecuteCommand;
import com.epion_t3.core.common.context.ExecuteContext;
import com.epion_t3.core.common.bean.ExecuteFlow;
import com.epion_t3.core.common.bean.ExecuteScenario;
import com.epion_t3.core.custom.validator.CommandValidator;
import com.epion_t3.core.exception.CommandValidationException;
import com.epion_t3.core.exception.SystemException;
import com.epion_t3.core.message.impl.CoreMessages;
import com.epion_t3.core.common.bean.scenario.Command;
import com.epion_t3.core.common.bean.scenario.Configuration;
import com.epion_t3.core.common.type.CommandStatus;
import com.epion_t3.core.common.type.ReferenceVariableType;
import com.epion_t3.core.common.type.ScenarioScopeVariables;
import com.epion_t3.core.common.util.BindUtils;
import com.epion_t3.core.common.util.DateTimeUtils;
import com.epion_t3.core.common.util.EvidenceUtils;
import com.epion_t3.core.common.util.IDUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * コマンド実行の基底クラス.
 *
 * @param <COMMAND>
 * @author takashno
 */
public abstract class AbstractCommandRunner<COMMAND extends Command>
        implements CommandRunner<
        COMMAND, Context, ExecuteContext, ExecuteScenario, ExecuteFlow, ExecuteCommand> {

    /**
     * コマンド.
     */
    private COMMAND command;

    /**
     * コンテキスト.
     */
    private Context context;

    /**
     * 実行情報.
     */
    private ExecuteContext executeContext;

    /**
     * シナリオ実行情報.
     */
    private ExecuteScenario executeScenario;

    /**
     * Flow実行情報.
     */
    private ExecuteFlow executeFlow;

    /**
     * コマンド実行情報.
     */
    private ExecuteCommand executeCommand;

    /**
     * コマンド実行処理.
     *
     * @param command         実行するコマンド
     * @param context         コンテキスト
     * @param executeContext  実行コンテキスト
     * @param executeScenario 実行シナリオ情報
     * @param executeFlow     実行Flow情報
     * @param executeCommand  実行コマンド情報
     * @param logger          ロガー
     * @throws Exception 例外
     */
    @Override
    public void execute(
            final COMMAND command,
            final Context context,
            final ExecuteContext executeContext,
            final ExecuteScenario executeScenario,
            final ExecuteFlow executeFlow,
            final ExecuteCommand executeCommand,
            final Logger logger) throws Exception {

        // インスタンス変数に登録
        this.command = command;
        this.context = context;
        this.executeContext = executeContext;
        this.executeScenario = executeScenario;
        this.executeFlow = executeFlow;
        this.executeCommand = executeCommand;

        // エラー
        Throwable error = null;

        // コマンド実行
        CommandResult result = null;

        try {

            executeScenario.getNotifications().addAll(
                    CommandValidator.getInstance().validate(
                            context, executeContext, executeScenario, executeFlow, command));

            if (executeScenario.hasErrorNotification()) {
                throw new CommandValidationException();
            }

            result = execute(command, logger);

        } catch (Throwable t) {

            logger.debug("Error Occurred...", t);
            error = t;
            result = new CommandResult();
            result.setMessage(t.getMessage());
            result.setStatus(CommandStatus.ERROR);
            throw t;

        } finally {

            // コマンド結果を設定
            executeCommand.setCommandResult(result);

            // 正常終了時にのみカスタムレポートを許可する
            if (error == null && !executeScenario.getOption().getNoreport()) {

                Class reporterClazz = executeCommand.getCommandInfo().getReporter();

                if (!NoneCommandReporter.class.isAssignableFrom(reporterClazz)) {

                    CommandReporter reporter = (CommandReporter) reporterClazz.newInstance();

                    // レポート出力
                    reporter.report(
                            command,
                            result,
                            context,
                            executeContext,
                            executeScenario,
                            executeFlow,
                            executeCommand,
                            error);

                } else {
                    logger.debug("not exists CommandReporter.");
                }

            }

        }

    }

    /**
     * プロファイル定数を取得.
     *
     * @return プロファイル定数マップ
     */
    protected Map<String, String> getProfileConstants() {
        return executeScenario.getProfileConstants();
    }

    /**
     * グローバル変数マップを取得.
     *
     * @return グローバル変数マップ
     */
    protected Map<String, Object> getGlobalScopeVariables() {
        return executeContext.getGlobalVariables();
    }

    /**
     * シナリオ変数マップを取得.
     *
     * @return シナリオ変数マップ
     */
    protected Map<String, Object> getScenarioScopeVariables() {
        return executeScenario.getScenarioVariables();
    }

    /**
     * Flow変数マップを取得.
     *
     * @return Flow変数マップ
     */
    protected Map<String, Object> getFlowScopeVariables() {
        return executeFlow.getFlowVariables();
    }

    /**
     * エビデンスマップ取得.
     *
     * @return エビデンスマップ
     */
    protected Map<String, EvidenceInfo> getEvidences() {
        return executeScenario.getEvidences();
    }

    /**
     * 変数を解決する.
     *
     * @param referenceVariable 参照変数
     * @return 解決した値
     */
    protected <V> V resolveVariables(
            final String referenceVariable) {

        if (StringUtils.isEmpty(referenceVariable)) {
            throw new SystemException(CoreMessages.CORE_ERR_0022);
        }

        // 正規表現からスコープと変数名に分割して解析する
        Matcher m = EXTRACT_PATTERN.matcher(referenceVariable);

        if (m.find()) {

            // 変数参照スコープを解決
            ReferenceVariableType referenceVariableType = ReferenceVariableType.valueOfByName(m.group(1));

            Object referObject = null;

            if (referenceVariableType != null) {
                switch (referenceVariableType) {
                    case FIX:
                        // 固定値の場合はそのまま返却
                        referObject = m.group(2);
                        break;
                    case GLOBAL:
                        // グローバル変数から解決
                        referObject = executeContext.getGlobalVariables().get(m.group(2));
                        break;
                    case SCENARIO:
                        // シナリオ変数から解決
                        referObject = executeScenario.getScenarioVariables().get(m.group(2));
                        break;
                    case FLOW:
                        // Flow変数から解決
                        referObject = executeFlow.getFlowVariables().get(m.group(2));
                        break;
                    default:
                        // 変数解決できない場合は、エラー
                        throw new SystemException(CoreMessages.CORE_ERR_0005, m.group(1));
                }
                if (referObject == null) {
                    return null;
                }
                return (V) referObject;
            } else {
                throw new SystemException(CoreMessages.CORE_ERR_0005, m.group(1));
            }
        }
        return null;
    }

    /**
     * @param target
     * @param value
     */
    protected void setVariable(final String target, final Object value) {
        Matcher m = EXTRACT_PATTERN.matcher(target);
        if (m.find()) {
            ReferenceVariableType referenceVariableType = ReferenceVariableType.valueOfByName(m.group(1));
            if (referenceVariableType != null) {
                switch (referenceVariableType) {
                    case FIX:
                        // Ignore
                        break;
                    case GLOBAL:
                        getGlobalScopeVariables().put(m.group(2), value);
                        break;
                    case SCENARIO:
                        getScenarioScopeVariables().put(m.group(2), value);
                        break;
                    case FLOW:
                        getFlowScopeVariables().put(m.group(2), value);
                        break;
                    default:
                        throw new SystemException(CoreMessages.CORE_ERR_0005, m.group(1));
                }
            } else {
                throw new SystemException(CoreMessages.CORE_ERR_0005, m.group(1));
            }
        }
    }

    protected void removeVariable(final String target) {
        Matcher m = EXTRACT_PATTERN.matcher(target);
        if (m.find()) {
            ReferenceVariableType referenceVariableType =
                    ReferenceVariableType.valueOfByName(m.group(1));
            if (referenceVariableType != null) {
                switch (referenceVariableType) {
                    case FIX:
                        // Ignore
                        break;
                    case GLOBAL:
                        getGlobalScopeVariables().remove(m.group(2));
                        break;
                    case SCENARIO:
                        getScenarioScopeVariables().remove(m.group(2));
                        break;
                    case FLOW:
                        getFlowScopeVariables().remove(m.group(2));
                        break;
                    default:
                        throw new SystemException(CoreMessages.CORE_ERR_0005, m.group(1));
                }
            } else {
                throw new SystemException(CoreMessages.CORE_ERR_0005, m.group(1));
            }
        }
    }

    /**
     * 実行中のコマンドが属するシナリオ格納ディレクトリを取得する.
     * コマンドに定義されているファイル等を参照する場合には、このメソッドで解決したパスからの相対パスを利用する.
     *
     * @return シナリオ配置パス
     */
    protected String getCommandBelongScenarioDirectory() {
        // シナリオ識別子はシステムで投入する値のため、存在しないことは不正
        String belongScenarioId = IDUtils.getInstance().extractBelongScenarioIdFromFqcn(executeCommand.getFqcn());
        if (StringUtils.isEmpty(belongScenarioId)) {
            throw new SystemException(CoreMessages.CORE_ERR_0018, executeCommand.getFqcn());
        }
        if (context.getOriginal().getScenarioPlacePaths().containsKey(belongScenarioId)) {
            Path belongScenarioPath = context.getOriginal().getScenarioPlacePaths().get(belongScenarioId);
            if (Files.notExists(belongScenarioPath)) {
                throw new SystemException(CoreMessages.CORE_ERR_0017, belongScenarioId.toString());
            }
            return belongScenarioPath.toString();
        } else {
            throw new SystemException(CoreMessages.CORE_ERR_0016, belongScenarioId);
        }
    }

    /**
     * 実行中のコマンドが属するシナリオ格納ディレクトリを取得する.
     * コマンドに定義されているファイル等を参照する場合には、このメソッドで解決したパスからの相対パスを利用する.
     *
     * @return
     */
    protected Path getCommandBelongScenarioDirectoryPath() {
        String belongScenarioId = IDUtils.getInstance().extractBelongScenarioIdFromFqcn(executeCommand.getFqcn());
        return context.getOriginal().getScenarioPlacePaths().get(belongScenarioId);
    }

    /**
     * シナリオ格納ディレクトリを取得する.
     *
     * @return シナリオ格納ディレクトリパス（文字列）
     */
    protected String getScenarioDirectory() {
        Object scenarioDirectory = executeScenario.getScenarioVariables()
                .get(ScenarioScopeVariables.SCENARIO_DIR.getName());
        if (scenarioDirectory == null) {
            throw new SystemException(CoreMessages.CORE_ERR_0019, executeScenario.getFqsn());
        }
        return scenarioDirectory.toString();
    }

    /**
     * シナリオ格納ディレクトリを取得する.
     *
     * @return シナリオ格納ディレクトリパス
     */
    protected Path getScenarioDirectoryPath() {
        Object scenarioDirectory = executeScenario.getScenarioVariables()
                .get(ScenarioScopeVariables.SCENARIO_DIR.getName());
        if (scenarioDirectory == null) {
            throw new SystemException(CoreMessages.CORE_ERR_0019, executeScenario.getFqsn());
        }
        return Path.class.cast(scenarioDirectory);
    }

    /**
     * エビデンス格納ディレクトリを取得する.
     *
     * @return エビデンス格納ディレクトリ
     */
    protected String getEvidenceDirectory() {
        Object evidenceDirectory = executeScenario.getScenarioVariables()
                .get(ScenarioScopeVariables.EVIDENCE_DIR.getName());
        if (evidenceDirectory == null) {
            throw new SystemException(CoreMessages.CORE_ERR_0020, executeScenario.getFqsn());
        }
        Path evidenceDirectoryPath = Path.class.cast(evidenceDirectory);
        if (Files.notExists(evidenceDirectoryPath)) {
            throw new SystemException(CoreMessages.CORE_ERR_0021, evidenceDirectoryPath.toString());
        }
        return evidenceDirectoryPath.toString();
    }

    /**
     * エビデンス格納ディレクトリを取得する.
     *
     * @return エビデンス格納ディレクトリ
     */
    protected Path getEvidenceDirectoryPath() {
        Object evidenceDirectory = executeScenario.getScenarioVariables()
                .get(ScenarioScopeVariables.EVIDENCE_DIR.getName());
        if (evidenceDirectory == null) {
            throw new SystemException(CoreMessages.CORE_ERR_0020, executeScenario.getFqsn());
        }
        Path evidenceDirectoryPath = Path.class.cast(evidenceDirectory);
        if (Files.notExists(evidenceDirectoryPath)) {
            throw new SystemException(CoreMessages.CORE_ERR_0021, evidenceDirectoryPath.toString());
        }
        return evidenceDirectoryPath;
    }

    /**
     * エビデンスのパスを取得する.
     * 基本的に、エビデンスの保存を行うパスについては、本メソッドで取得したパスを利用すること.
     *
     * @param baseName ファイル名
     * @return エビデンスの格納パス
     */
    protected Path getEvidencePath(
            String baseName) {
        return Paths.get(
                getEvidenceDirectory(),
                getEvidenceBaseName(baseName));
    }

    /**
     * 【移譲メソッド】
     * ファイルエビデンスを登録する.
     *
     * @param evidence エビデンスパス
     */
    protected void registrationFileEvidence(
            Path evidence) {
        EvidenceUtils.getInstance().registrationFileEvidence(
                executeScenario, executeFlow, evidence);
    }

    /**
     * 【移譲メソッド】
     * FlowIDからファイルエビデンスのPathを参照.
     *
     * @param flowId
     * @return
     */
    protected Path referFileEvidence(String flowId) {
        return EvidenceUtils.getInstance().referFileEvidence(executeScenario, flowId);
    }

    /**
     * 【移譲メソッド】
     * オブジェクトエビデンスを登録する.
     *
     * @param evidence オブジェクトエビデンス
     */
    protected void registrationObjectEvidence(
            Object evidence) {
        EvidenceUtils.getInstance().registrationObjectEvidence(
                executeContext, executeScenario, executeFlow, evidence);
    }

    /**
     * 【移譲メソッド】
     * FlowIDからオブジェクトエビデンスを参照.
     * このオブジェクトはクローンであるためエビデンス原本ではない.
     *
     * @param flowId FlowID
     * @param <O>    オブジェクト
     * @return オブジェクトエビデンス
     */
    protected <O extends Serializable> O referObjectEvidence(String flowId) {
        return EvidenceUtils.getInstance().referObjectEvidence(
                executeContext, executeScenario, flowId);
    }

    /**
     * 【移譲メソッド】
     * エビデンス名を取得する.
     *
     * @return エビデンス基底名
     */
    protected String getEvidenceBaseName(String baseName) {
        return EvidenceUtils.getInstance().getEvidenceBaseName(executeFlow, baseName);
    }

    /**
     * 設定情報を参照.
     *
     * @param <C>
     * @return 設定情報
     */
    protected <C extends Configuration> C referConfiguration(String configurationId) {

        // 参照用の設定ID
        String referConfigurationId = configurationId;

        // 設定識別子かどうか判断
        if (!IDUtils.getInstance().isFullQueryConfigurationId(configurationId)) {
            // 設定識別子を作成（シナリオID＋設定ID）
            // シナリオIDは現在実行しているシナリオから取得
            referConfigurationId = IDUtils.getInstance().createFullConfigurationId(
                    executeScenario.getInfo().getId(), referConfigurationId);
        }
        Configuration configuration =
                context.getOriginal().getConfigurations().get(configurationId);
        if (configuration == null) {
            throw new SystemException(CoreMessages.CORE_ERR_0006, configurationId);
        }

        Configuration cloneConfiguration = SerializationUtils.clone(configuration);

        // 変数バインド
        BindUtils.getInstance().bind(
                cloneConfiguration,
                executeScenario.getProfileConstants(),
                executeContext.getGlobalVariables(),
                executeScenario.getScenarioVariables(),
                executeFlow.getFlowVariables()
        );

        return (C) cloneConfiguration;
    }

    /**
     * 【移譲メソッド】
     * バインド.
     *
     * @param target
     * @return
     */
    protected String bind(String target) {
        // 変数バインド
        return BindUtils.getInstance().bind(
                target,
                executeScenario.getProfileConstants(),
                executeContext.getGlobalVariables(),
                executeScenario.getScenarioVariables(),
                executeFlow.getFlowVariables());
    }

    /**
     * 【移譲メソッド】
     * バインド.
     *
     * @param target
     * @return
     */
    protected void bind(Object target) {
        // 変数バインド
        BindUtils.getInstance().bind(
                target,
                executeScenario.getProfileConstants(),
                executeContext.getGlobalVariables(),
                executeScenario.getScenarioVariables(),
                executeFlow.getFlowVariables());
    }

    protected LocalDateTime referFlowStartDate(String flowId) {
        return DateTimeUtils.getInstance().referFlowStartDate(executeScenario, flowId);
    }

    protected LocalDateTime referFlowEndDate(String flowId) {
        return DateTimeUtils.getInstance().referFlowEndDate(executeScenario, flowId);
    }

}
