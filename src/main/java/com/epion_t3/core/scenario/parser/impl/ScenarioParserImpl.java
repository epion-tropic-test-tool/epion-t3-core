package com.epion_t3.core.scenario.parser.impl;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.epion_t3.core.common.bean.scenario.Command;
import com.epion_t3.core.common.bean.scenario.Configuration;
import com.epion_t3.core.common.bean.scenario.ET3Base;
import com.epion_t3.core.common.context.Context;
import com.epion_t3.core.common.context.ExecuteContext;
import com.epion_t3.core.common.type.NotificationType;
import com.epion_t3.core.common.type.StageType;
import com.epion_t3.core.common.util.IDUtils;
import com.epion_t3.core.exception.*;
import com.epion_t3.core.message.MessageManager;
import com.epion_t3.core.message.impl.CoreMessages;
import com.epion_t3.core.scenario.bean.ScenarioParseError;
import com.epion_t3.core.scenario.bean.ScenarioValidateError;
import com.epion_t3.core.scenario.parser.ScenarioParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.bval.jsr.ApacheValidationProvider;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * シナリオ解析処理.
 * カスタム機能および全シナリオの解析を行う処理クラス.
 */
@Slf4j
public final class ScenarioParserImpl implements ScenarioParser<Context, ExecuteContext> {

    /**
     * インスタンス.
     */
    private static final ScenarioParserImpl instance = new ScenarioParserImpl();

    /**
     * シナリオファイルパターン（正規表現文字列）.
     */
    private static final String FILENAME_REGEXP_PATTERN = "et3_.*\\.yaml";

    /**
     * シナリオファイルパターン（正規表現）
     */
    private static final Pattern SCENARIO_FILENAME_PATTERN = Pattern.compile(FILENAME_REGEXP_PATTERN);

    /**
     * 単項目チェックValidatorFactory.
     */
    private ValidatorFactory validationFactory =
            Validation.byProvider(ApacheValidationProvider.class).configure().buildValidatorFactory();

    /**
     * プライベートコンストラクタ.
     */
    private ScenarioParserImpl() {
        // Do Nothing...
    }

    /**
     * インスタンスを取得する.
     *
     * @return
     */
    public static ScenarioParserImpl getInstance() {
        return instance;
    }

    /**
     * 解析処理.
     *
     * @param context
     */
    @Override
    public void parse(final Context context, final ExecuteContext executeContext) {

        // シナリオ解析ステージ
        executeContext.setStage(StageType.PARSE_SCENARIO);

        // ルートディレクトリの存在チェック
        if (!Files.exists(Paths.get(context.getOption().getRootPath()))) {
            throw new SystemException(CoreMessages.CORE_ERR_0009, context.getOption().getRootPath());
        }

        // Bean Validator
        Validator validator = validationFactory.getValidator();

        FileVisitor<Path> visitor = new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

                Matcher fileNameMatcher = SCENARIO_FILENAME_PATTERN.matcher(file.getFileName().toString());

                if (!fileNameMatcher.matches()) {
                    log.debug("skip file: {}, scenario file must be name pattern: {}", file, FILENAME_REGEXP_PATTERN);
                    return FileVisitResult.CONTINUE;
                }

                log.debug("visit file: {}", file);

                ET3Base t3Base = null;
                try {

                    // YAML -> Object
                    t3Base = context.getObjectMapper().readValue(file.toFile(), ET3Base.class);

                    // Bean Validation
                    Set<ConstraintViolation<ET3Base>> validationErrors = validator.validate(t3Base);

                    for (ConstraintViolation violation : validationErrors) {
                        executeContext.addNotification(ScenarioValidateError.scenarioValidateErrorBuilder()
                                .stage(executeContext.getStage()).level(NotificationType.ERROR)
                                .filePath(file.toString())
                                .target(violation.getPropertyPath().toString())
                                .value(violation.getInvalidValue())
                                .message(MessageManager.getInstance().getMessage(
                                        CoreMessages.CORE_ERR_0011, file.toString(),
                                        violation.getPropertyPath().toString(), violation.getInvalidValue()))
                                .build());
                    }


                } catch (JsonParseException e) {
                    log.debug("Error Occurred...", e);
                    executeContext.addNotification(
                            ScenarioParseError.scenarioParseErrorBuilder().stage(executeContext.getStage()).level(NotificationType.ERROR)
                                    .error(e).filePath(file.toString())
                                    .message(MessageManager.getInstance().getMessage(
                                            CoreMessages.CORE_ERR_0011, file.toString(), e.getMessage())).build());
                    return FileVisitResult.CONTINUE;

                } catch (JsonMappingException e) {
                    log.debug("Error Occurred...", e);
                    Throwable cause = e.getCause();
                    if (cause != null) {
                        Class causeClass = cause.getClass();
                        if (CommandNotFoundException.class.isAssignableFrom(causeClass)) {
                            // コマンドが見つからない場合
                            CommandNotFoundException cnfe = CommandNotFoundException.class.cast(cause);
                            executeContext.addNotification(
                                    ScenarioParseError.scenarioParseErrorBuilder().stage(executeContext.getStage()).level(NotificationType.ERROR)
                                            .error(e).filePath(file.toString())
                                            .message(MessageManager.getInstance().getMessage(
                                                    CoreMessages.CORE_ERR_0033, file.toString(), cnfe.getCommandId())).build());
                        } else if (FlowNotFoundException.class.isAssignableFrom(causeClass)) {
                            // Flowが見つからない場合
                            FlowNotFoundException fnfe = FlowNotFoundException.class.cast(cause);
                            executeContext.addNotification(
                                    ScenarioParseError.scenarioParseErrorBuilder().stage(executeContext.getStage()).level(NotificationType.ERROR)
                                            .error(e).filePath(file.toString())
                                            .message(MessageManager.getInstance().getMessage(
                                                    CoreMessages.CORE_ERR_0034, file.toString(), fnfe.getFlowId())).build());
                        } else if (ConfigurationNotFoundException.class.isAssignableFrom(causeClass)) {
                            // 設定が見つからない場合
                            ConfigurationNotFoundException cnfe = ConfigurationNotFoundException.class.cast(cause);
                            executeContext.addNotification(
                                    ScenarioParseError.scenarioParseErrorBuilder().stage(executeContext.getStage()).level(NotificationType.ERROR)
                                            .error(e).filePath(file.toString())
                                            .message(MessageManager.getInstance().getMessage(
                                                    CoreMessages.CORE_ERR_0035, file.toString(), cnfe.getConfigurationId())).build());
                        } else {
                            executeContext.addNotification(
                                    ScenarioParseError.scenarioParseErrorBuilder().stage(executeContext.getStage()).level(NotificationType.ERROR)
                                            .error(e).filePath(file.toString())
                                            .message(MessageManager.getInstance().getMessage(
                                                    CoreMessages.CORE_ERR_0011, file.toString(), cause.getMessage())).build());
                        }
                    } else {
                        executeContext.addNotification(
                                ScenarioParseError.scenarioParseErrorBuilder().stage(executeContext.getStage()).level(NotificationType.ERROR)
                                        .error(e).filePath(file.toString())
                                        .message(MessageManager.getInstance().getMessage(
                                                CoreMessages.CORE_ERR_0011, file.toString(), e.getMessage())).build());
                    }
                    return FileVisitResult.CONTINUE;
                }

                // Profileの保持
                if (t3Base.getProfiles() != null) {
                    for (Map.Entry<String, Map<String, String>> entry : t3Base.getProfiles().entrySet()) {
                        if (context.getOriginal().getProfiles().containsKey(entry.getKey())) {
                            context.getOriginal().getProfiles().get(entry.getKey()).putAll(entry.getValue());
                        } else {
                            context.getOriginal().getProfiles().put(entry.getKey(), entry.getValue());
                        }
                    }
                }

                if (t3Base.getInfo() != null) {

                    // process識別子とPathを紐付ける
                    context.getOriginal().getScenarioPlacePaths().put(t3Base.getInfo().getId(), file.getParent());

                    // ファイル原本の完全保存
                    context.getOriginal().getOriginals().put(t3Base.getInfo().getId(), t3Base);

                    // t3BaseがfinalでないのでLambdaが利用できない・・・
                    // なんかやり方あるのかね・・・
                    // コマンド読み込み
                    for (Command command : t3Base.getCommands()) {

                        Set<ConstraintViolation<Command>> result = validator.validate(command);

                        // コマンド識別子を作成
                        String fullCommandId = IDUtils.getInstance().createFullCommandId(t3Base.getInfo().getId(), command.getId());

                        // コマンド定義を追加
                        context.getOriginal().getCommands().put(
                                fullCommandId, command);

                        // コマンド識別子とシナリオIDを紐付ける
                        context.getOriginal().getCommandScenarioRelations().put(fullCommandId, t3Base.getInfo().getId());

                        // コマンド識別子とPathを紐付ける
                        context.getOriginal().getCommandPlacePaths().put(fullCommandId, file);

                    }

                    // 設定情報読み込み
                    for (Configuration configuration : t3Base.getConfigurations()) {

                        // 設定識別子を作成
                        String fullConfigurationId =
                                IDUtils.getInstance().createFullConfigurationId(
                                        t3Base.getInfo().getId(), configuration.getId());

                        // 設定定義を追加
                        context.getOriginal().getConfigurations().put(fullConfigurationId, configuration);

                        // 設定識別子とシナリオIDを紐付ける
                        context.getOriginal().getConfigurationScenarioRelations().put(
                                fullConfigurationId, t3Base.getInfo().getId());

                        // 設定識別子とPathを紐付ける
                        context.getOriginal().getConfigurationPlacePaths().put(fullConfigurationId, file);

                    }


                }

                return FileVisitResult.CONTINUE;
            }
        };

        try {

            Files.walkFileTree(Paths.get(context.getOption().getRootPath()), visitor);

        } catch (IOException e) {

            log.debug("Error Occurred...", e);

            throw new SystemException(e);

        } finally {

            if (executeContext.hasErrorNotification()) {
                throw new ScenarioParseException();
            }

        }
    }


}
