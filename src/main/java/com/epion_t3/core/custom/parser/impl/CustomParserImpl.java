package com.epion_t3.core.custom.parser.impl;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.reflect.ClassPath;
import com.epion_t3.core.command.handler.listener.CommandAfterListener;
import com.epion_t3.core.command.handler.listener.CommandBeforeListener;
import com.epion_t3.core.command.handler.listener.CommandErrorListener;
import com.epion_t3.core.command.handler.listener.holder.CommandListenerHolder;
import com.epion_t3.core.common.annotation.CommandDefinition;
import com.epion_t3.core.common.annotation.CommandListener;
import com.epion_t3.core.common.annotation.CustomConfigurationDefinition;
import com.epion_t3.core.common.annotation.FlowDefinition;
import com.epion_t3.core.common.bean.*;
import com.epion_t3.core.common.bean.scenario.Command;
import com.epion_t3.core.common.bean.scenario.Configuration;
import com.epion_t3.core.common.bean.scenario.ET3Base;
import com.epion_t3.core.common.bean.scenario.Flow;
import com.epion_t3.core.common.bean.spec.ET3Spec;
import com.epion_t3.core.common.bean.spec.Structure;
import com.epion_t3.core.common.context.Context;
import com.epion_t3.core.common.context.ExecuteContext;
import com.epion_t3.core.common.type.NotificationType;
import com.epion_t3.core.common.type.StageType;
import com.epion_t3.core.custom.holder.CustomPackageHolder;
import com.epion_t3.core.custom.parser.CustomParser;
import com.epion_t3.core.custom.validator.CustomSpecValidator;
import com.epion_t3.core.exception.ScenarioParseException;
import com.epion_t3.core.exception.SystemException;
import com.epion_t3.core.message.MessageManager;
import com.epion_t3.core.message.impl.CoreMessages;
import lombok.extern.slf4j.Slf4j;
import org.apache.bval.jsr.ApacheValidationProvider;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * カスタム機能の定義を解析するクラス.
 *
 * @author takashno
 */
@Slf4j
public final class CustomParserImpl implements CustomParser<Context, ExecuteContext> {

    /**
     * シングルトンインスタンス.
     */
    private static final CustomParserImpl instance = new CustomParserImpl();

    /**
     * カスタム機能定義のファイルパターン（正規表現）.
     */
    private static final String CUSTOM_FILENAME_REGEXP_PATTERN = "et3_.*[\\-]?custom.yaml";

    /**
     * カスタム機能の設計定義のファイル名パターン（文字列フォーマット）.
     */
    private static final String CUSTOM_SPEC_FILENAME_PATTERN = "et3_%s_spec_config.yaml";

    /**
     * 単項目チェックValidatorFactory.
     */
    private ValidatorFactory validationFactory =
            Validation.byProvider(ApacheValidationProvider.class).configure().buildValidatorFactory();

    /**
     * プライベートコンストラクタ.
     */
    private CustomParserImpl() {
        // Do Nothing...
    }

    /**
     * インスタンスを取得する.
     *
     * @return
     */
    public static CustomParserImpl getInstance() {
        return instance;
    }


    /**
     * {@inheritDoc}
     *
     * @param context
     */
    @Override
    public void parse(final Context context, final ExecuteContext executeContext) {

        // カスタム解析ステージ
        executeContext.setStage(StageType.PARSE_CUSTOM);

        // 読み込み対象のカスタム機能の一覧を取得する
        findCustom(context, executeContext);

        // ---------------------
        // カスタム設計を解析
        // ---------------------
        parseCustomSpec(context, executeContext);

        // エラー判定
        // この時点でエラーが存在する場合は、継続しない
        if (!executeContext.getNotifications().isEmpty()) {
            throw new ScenarioParseException();
        }

        // ---------------------
        // カスタム機能を解析
        // ---------------------
        parseCustomFunction(context, executeContext);

        // エラー判定
        // この時点でエラーが存在する場合は、継続しない
        if (!executeContext.getNotifications().isEmpty()) {
            throw new ScenarioParseException();
        }

    }

    /**
     * カスタム機能定義を見つける.
     *
     * @param context コンテキスト
     */
    private void findCustom(final Context context, final ExecuteContext executeContext) {

        try {
            // 再帰的にカスタム定義ファイルを見つける
            Files.find(Paths.get(context.getOption().getRootPath()),
                    Integer.MAX_VALUE, (p, attr) -> p.toFile().getName().matches(CUSTOM_FILENAME_REGEXP_PATTERN))
                    .forEach(x -> {
                        try {
                            ET3Base custom = context.getObjectMapper().readValue(x.toFile(), ET3Base.class);
                            if (custom.getCustoms() != null) {
                                custom.getCustoms().getPackages().forEach((k, v) -> {
                                    CustomPackageHolder.getInstance().addCustomPackage(k, v);
                                });
                            } else {
                                // カスタム機能定義が未指定の場合は、WARNで警告をしておく.
                                executeContext.addNotification(
                                        ET3Notification.builder()
                                                .stage(executeContext.getStage())
                                                .level(NotificationType.WARN)
                                                .message(MessageManager.getInstance()
                                                        .getMessage(CoreMessages.CORE_WRN_0003)).build());
                            }
                        } catch (IOException e) {
                            executeContext.addNotification(
                                    ET3Notification.builder()
                                            .stage(executeContext.getStage())
                                            .level(NotificationType.ERROR)
                                            .message(MessageManager.getInstance()
                                                    .getMessage(CoreMessages.CORE_ERR_0032)).build());
                        }
                    });
        } catch (IOException e) {
            executeContext.addNotification(ET3Notification.builder()
                    .stage(executeContext.getStage())
                    .level(NotificationType.ERROR)
                    .error(e)
                    .message(MessageManager.getInstance().getMessage(CoreMessages.CORE_ERR_0032)).build());
        }

    }


    /**
     * カスタム機能設計の解析.
     *
     * @param context コンテキスト
     */
    private void parseCustomSpec(final Context context, final ExecuteContext executeContext) {

        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        Map<String, String> customPackages = CustomPackageHolder.getInstance().getCustomPackages();

        // カスタム機能のパッケージを走査
        for (Map.Entry<String, String> entry : customPackages.entrySet()) {

            log.debug("start parse custom package spec -> {}:{}", entry.getKey(), entry.getValue());

            // カスタム機能の設計データを読み込み
            String specFileName = String.format(CUSTOM_SPEC_FILENAME_PATTERN, entry.getKey());

            InputStream isis = CustomParserImpl.class.getClassLoader().getResourceAsStream(specFileName);

            try (InputStream is = loader.getResourceAsStream(specFileName)) {

                if (is == null) {
                    executeContext.addNotification(ET3Notification.builder()
                            .stage(executeContext.getStage())
                            .level(NotificationType.ERROR)
                            .message(MessageManager.getInstance()
                                    .getMessage(CoreMessages.CORE_ERR_0037, entry.getKey(), entry.getValue())).build());
                    continue;
                }


                ET3Spec et3Spec = context.getObjectMapper().readValue(is, ET3Spec.class);


                Set<ConstraintViolation<ET3Spec>> validationErrors = validationFactory.getValidator().validate(et3Spec);


                // カスタム設計情報の読み込み
                CustomSpecInfo customSpecInfo = new CustomSpecInfo();
                CustomPackageHolder.getInstance().addCustomSpec(entry.getKey(), customSpecInfo);

                // カスタム名
                customSpecInfo.setName(entry.getKey());

                // カスタムパッケージ
                customSpecInfo.setCustomPackage(entry.getValue());

                // 概要
                et3Spec.getInfo().getSummary().stream()
                        .forEach(x -> customSpecInfo.putSummary(x.getLang(), x.getContents()));

                // 詳細
                et3Spec.getInfo().getDescription().stream()
                        .forEach(x -> customSpecInfo.putDescription(x.getLang(), x.getContents()));

                // カスタムコマンド設定
                et3Spec.getCommands().stream().forEach(
                        x -> {
                            // コマンド設計を作成
                            CommandSpecInfo commandSpecInfo = new CommandSpecInfo();
                            commandSpecInfo.setId(x.getId());

                            // 機能をLocale毎に分けて設定
                            x.getSummary().stream().forEach(y -> commandSpecInfo.addFunction(y.getLang(), y.getContents()));

                            // 試験項目をorderでソートしたのち、Locale毎に分けて設定
                            x.getTestItem().stream().sorted(Comparator.comparing(ti -> ti.getOrder()))
                                    .forEach(ti -> ti.getSummary()
                                            .forEach(c -> commandSpecInfo.addTestItem(c.getLang(), c.getContents())));

                            // コマンド構成を設定
                            x.getStructure().stream().sorted(Comparator.comparing(s -> s.getOrder()))
                                    .forEach(s -> {
                                        CommandSpecStructure commandSpecStructure = new CommandSpecStructure();
                                        commandSpecStructure.setName(s.getName());
                                        commandSpecStructure.setRequired(s.getRequired());
                                        commandSpecStructure.setPattern(s.getPattern());
                                        commandSpecStructure.setType(s.getType());
                                        s.getSummary().stream()
                                                .forEach(sm -> commandSpecStructure.putSummary(sm.getLang(), sm.getContents()));
                                        if (s.getDescription() != null) {
                                            s.getDescription().stream()
                                                    .forEach(sm -> commandSpecStructure.putDescription(sm.getLang(), sm.getContents()));
                                        }
                                        if (s.getProperty() != null && !s.getProperty().isEmpty()) {
                                            parseCustomCommandStructureRecursive(commandSpecStructure, s.getProperty());
                                        }
                                        commandSpecInfo.addStructure(commandSpecStructure);
                                    });

                            // コマンド追加
                            customSpecInfo.putCommandSpec(commandSpecInfo);
                        }
                );

                // メッセージ設定
                et3Spec.getMessages().stream().forEach(
                        x -> {
                            x.getMessage().forEach(y ->
                                    customSpecInfo.addMessage(y.getLang(), x.getId(), y.getContents()));
                        });


            } catch (JsonMappingException e) {
                executeContext.addNotification(ET3Notification.builder()
                        .stage(executeContext.getStage())
                        .level(NotificationType.ERROR)
                        .error(e)
                        .message(MessageManager.getInstance()
                                .getMessage(CoreMessages.CORE_ERR_0036, entry.getKey(), entry.getValue())).build());
            } catch (IOException e) {
                executeContext.addNotification(ET3Notification.builder()
                        .stage(executeContext.getStage())
                        .level(NotificationType.ERROR)
                        .error(e)
                        .message(MessageManager.getInstance()
                                .getMessage(CoreMessages.CORE_ERR_0024, entry.getKey(), entry.getValue())).build());
            }

        }

    }

    private void parseCustomCommandStructureRecursive(CommandSpecStructure parent, List<Structure> structures) {

        // コマンド構成を設定
        structures.stream().sorted(Comparator.comparing(s -> s.getOrder()))
                .forEach(s -> {
                    CommandSpecStructure commandSpecStructure = new CommandSpecStructure();
                    commandSpecStructure.setName(s.getName());
                    commandSpecStructure.setRequired(s.getRequired());
                    commandSpecStructure.setPattern(s.getPattern());
                    commandSpecStructure.setType(s.getType());
                    s.getSummary().stream()
                            .forEach(sm -> commandSpecStructure.putSummary(sm.getLang(), sm.getContents()));
                    if (s.getDescription() != null) {
                        s.getDescription().stream()
                                .forEach(sm -> commandSpecStructure.putDescription(sm.getLang(), sm.getContents()));
                    }
                    if (s.getProperty() != null && !s.getProperty().isEmpty()) {
                        parseCustomCommandStructureRecursive(commandSpecStructure, s.getProperty());
                    }
                    parent.getProperty().add(commandSpecStructure);
                });
    }

    /**
     * カスタムコマンド解析.
     *
     * @param context コンテキスト
     */
    private void parseCustomFunction(final Context context, final ExecuteContext executeContext) {

        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        Map<String, String> customPackages = CustomPackageHolder.getInstance().getCustomPackages();

        // カスタム機能のパッケージを走査
        for (Map.Entry<String, String> entry : customPackages.entrySet()) {

            log.debug("start parse custom package function -> {}:{}", entry.getKey(), entry.getValue());

            // カスタム機能の指定パッケージ配下の全てのクラスを取得
            Set<Class<?>> allClasses = null;
            try {
                allClasses = ClassPath.from(loader)
                        .getTopLevelClassesRecursive(entry.getValue()).stream()
                        .map(info -> info.load())
                        .collect(Collectors.toSet());
            } catch (IOException e) {
                throw new SystemException(e, CoreMessages.CORE_ERR_0025);
            }


            for (Class<?> clazz : allClasses) {

                if (clazz.getDeclaredAnnotation(CommandDefinition.class) != null
                        && Command.class.isAssignableFrom(clazz)) {
                    // カスタムコマンドを解析
                    CommandDefinition command = clazz.getDeclaredAnnotation(CommandDefinition.class);
                    CommandInfo commandInfo = CommandInfo.builder().id(command.id()).model(clazz)
                            .assertCommand(command.assertCommand())
                            .runner(command.runner())
                            .reporter(command.reporter()).build();
                    CustomPackageHolder.getInstance().addCustomCommandInfo(
                            command.id(), commandInfo);

                    // コマンドの設計と実装の検証
                    executeContext.getNotifications().addAll(CustomSpecValidator.getInstance().validateCommandSpec(
                            context, executeContext, entry.getKey(), commandInfo));


                } else if (clazz.getDeclaredAnnotation(FlowDefinition.class) != null
                        && Flow.class.isAssignableFrom(clazz)) {
                    // カスタムFlowを解析
                    FlowDefinition flow =
                            clazz.getDeclaredAnnotation(FlowDefinition.class);
                    FlowInfo flowInfo = FlowInfo.builder().id(flow.id()).model(clazz).runner(flow.runner()).build();
                    CustomPackageHolder.getInstance().addCustomFlowInfo(
                            flow.id(), flowInfo);

                } else if (clazz.getDeclaredAnnotation(CustomConfigurationDefinition.class) != null
                        && Configuration.class.isAssignableFrom(clazz)) {
                    // カスタム設定を解析
                    CustomConfigurationDefinition configuration =
                            clazz.getDeclaredAnnotation(CustomConfigurationDefinition.class);
                    CustomConfigurationInfo customConfigurationInfo =
                            CustomConfigurationInfo.builder().id(configuration.id()).model(clazz).build();
                    CustomPackageHolder.getInstance().addCustomConfigurationInfo(customConfigurationInfo);

                } else if (CommandListener.class.isAssignableFrom(clazz)) {
                    // カスタムコマンドリスナーを解析
                    if (CommandBeforeListener.class.isAssignableFrom(clazz)) {
                        CommandListenerHolder.getInstance()
                                .addCommandBeforeListener((Class<CommandBeforeListener>) clazz);
                    } else if (CommandAfterListener.class.isAssignableFrom(clazz)) {
                        CommandListenerHolder.getInstance()
                                .addCommandAfterListener((Class<CommandAfterListener>) clazz);
                    } else if (CommandErrorListener.class.isAssignableFrom(clazz)) {
                        CommandListenerHolder.getInstance()
                                .addCommandErrorListener((Class<CommandErrorListener>) clazz);
                    }

                }


            }

            // >>他機能のカスタムがあれば随時追加<<

            log.debug("end parse custom package function -> {}:{}", entry.getKey(), entry.getValue());
        }

    }

}
