package com.zomu.t.epion.tropic.test.tool.core.custom.parser.impl;

import com.google.common.reflect.ClassPath;
import com.zomu.t.epion.tropic.test.tool.core.annotation.CommandDefinition;
import com.zomu.t.epion.tropic.test.tool.core.annotation.CommandListener;
import com.zomu.t.epion.tropic.test.tool.core.annotation.CustomConfigurationDefinition;
import com.zomu.t.epion.tropic.test.tool.core.annotation.FlowDefinition;
import com.zomu.t.epion.tropic.test.tool.core.command.handler.listener.CommandAfterListener;
import com.zomu.t.epion.tropic.test.tool.core.command.handler.listener.CommandBeforeListener;
import com.zomu.t.epion.tropic.test.tool.core.command.handler.listener.CommandErrorListener;
import com.zomu.t.epion.tropic.test.tool.core.context.*;
import com.zomu.t.epion.tropic.test.tool.core.exception.SystemException;
import com.zomu.t.epion.tropic.test.tool.core.exception.bean.ScenarioParseError;
import com.zomu.t.epion.tropic.test.tool.core.custom.parser.IndividualTargetParser;
import com.zomu.t.epion.tropic.test.tool.core.holder.CommandListenerHolder;
import com.zomu.t.epion.tropic.test.tool.core.holder.CustomConfigurationHolder;
import com.zomu.t.epion.tropic.test.tool.core.model.scenario.Configuration;
import com.zomu.t.epion.tropic.test.tool.core.model.scenario.Flow;
import com.zomu.t.epion.tropic.test.tool.core.type.ScenarioPaseErrorType;
import com.zomu.t.epion.tropic.test.tool.core.model.scenario.Command;
import com.zomu.t.epion.tropic.test.tool.core.exception.ScenarioParseException;
import com.zomu.t.epion.tropic.test.tool.core.holder.CustomPackageHolder;
import com.zomu.t.epion.tropic.test.tool.core.model.scenario.T3Base;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * カスタム機能の定義を解析するクラス.
 *
 * @author takashno
 */
@Slf4j
public final class BaseCustomParser implements IndividualTargetParser {

    /**
     * シングルトンインスタンス.
     */
    private static final BaseCustomParser instance = new BaseCustomParser();

    /**
     * カスタム機能定義のファイルパターン（正規表現）.
     */
    public static final String CUSTOM_FILENAME_REGEXP_PATTERN = "t3_.*[\\-]?custom.yaml";

    /**
     * プライベートコンストラクタ.
     */
    private BaseCustomParser() {
        // Do Nothing...
    }

    /**
     * インスタンスを取得する.
     *
     * @return
     */
    public static BaseCustomParser getInstance() {
        return instance;
    }

    /**
     * {@inheritDoc}
     *
     * @param context
     */
    @Override
    public void parse(Context context) {
        parse(context, CUSTOM_FILENAME_REGEXP_PATTERN);
    }

    /**
     * {@inheritDoc}
     *
     * @param context
     * @param fileNamePattern
     */
    @Override
    public void parse(final Context context, String fileNamePattern) {

        Context baseContext = Context.class.cast(context);

        findCustom(baseContext, fileNamePattern);

        // カスタム機能を解析する
        parseCustom(baseContext);

    }

    /**
     * カスタム機能定義を見つける.
     *
     * @param context
     * @param fileNamePattern
     */
    private void findCustom(final Context context, final String fileNamePattern) {

        try {
            // 再帰的にカスタム定義ファイルを見つける
            Files.find(Paths.get(context.getOption().getRootPath()),
                    Integer.MAX_VALUE, (p, attr) -> p.toFile().getName().matches(fileNamePattern)).forEach(x -> {
                try {
                    context.getOriginal().getCustom().getPackages()
                            .putAll(context.getObjectMapper().readValue(x.toFile(), T3Base.class).getCustoms().getPackages());
                } catch (IOException e) {
                    throw new ScenarioParseException(
                            ScenarioParseError.builder().filePath(x).type(ScenarioPaseErrorType.PARSE_ERROR)
                                    .message("Custom Package Config Parse Error Occurred.").build());
                }
            });
        } catch (IOException e) {
            throw new SystemException(e);
        }

    }

    /**
     * カスタムコマンド解析.
     *
     * @param context コンテキスト
     */
    private void parseCustom(Context context) {

        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        // カスタムコマンド
        context.getOriginal().getCustom().getPackages().forEach(
                (k, v) -> CustomPackageHolder.getInstance().addCustomPackage(k, v));

        // カスタム機能のパッケージを走査
        for (Map.Entry<String, String> entry : context.getOriginal().getCustom().getPackages().entrySet()) {


            log.debug("start parse custom function packages -> {}:{}", entry.getKey(), entry.getValue());

            // カスタム機能の指定パッケージ配下の全てのクラスを取得
            Set<Class<?>> allClasses = null;
            try {
                allClasses = ClassPath.from(loader)
                        .getTopLevelClassesRecursive(entry.getValue()).stream()
                        .map(info -> info.load())
                        .collect(Collectors.toSet());
            } catch (IOException e) {
                throw new SystemException(e);
            }

            // カスタムコマンドを解析
            allClasses.stream()
                    .filter(x -> x.getDeclaredAnnotation(CommandDefinition.class) != null)
                    .filter(x -> Command.class.isAssignableFrom(x))
                    .forEach(x -> {
                        CommandDefinition command = x.getDeclaredAnnotation(CommandDefinition.class);
                        CommandInfo commandInfo = CommandInfo.builder().id(command.id()).model(x)
                                .assertCommand(command.assertCommand())
                                .runner(command.runner())
                                .reporter(command.reporter()).build();
                        CustomPackageHolder.getInstance().addCustomCommandInfo(
                                command.id(), commandInfo);
                        // TODO:シナリオを動かすときに使うが、果たして重複保持が必要か？
                        context.getCustomCommands().put(command.id(), commandInfo);
                    });


            // カスタムFlowを解析
            allClasses.stream()
                    .filter(x -> x.getDeclaredAnnotation(FlowDefinition.class) != null)
                    .filter(x -> Flow.class.isAssignableFrom(x))
                    .forEach(x -> {
                        FlowDefinition flow =
                                x.getDeclaredAnnotation(FlowDefinition.class);
                        FlowInfo flowInfo = FlowInfo.builder().id(flow.id()).model(x).runner(flow.runner()).build();
                        CustomPackageHolder.getInstance().addCustomFlowInfo(
                                flow.id(), flowInfo);
                        // TODO:シナリオを動かすときに使うが、果たして重複保持が必要か？
                        context.getCustomFlows().put(flow.id(), flowInfo);
                    });

            // カスタム設定を解析
            allClasses.stream()
                    .filter(x -> x.getDeclaredAnnotation(CustomConfigurationDefinition.class) != null)
                    .filter(x -> Configuration.class.isAssignableFrom(x))
                    .forEach(x -> {
                        CustomConfigurationDefinition configuration =
                                x.getDeclaredAnnotation(CustomConfigurationDefinition.class);
                        CustomConfigurationInfo customConfigurationInfo =
                                CustomConfigurationInfo.builder().id(configuration.id()).model(x).build();
                        CustomConfigurationHolder.getInstance().addCustomConfigurationInfo(customConfigurationInfo);
                        // :いるっけ・・・？経緯を忘れすぎてよくわからん・・・
                        context.getCustomConfigurations().put(configuration.id(), customConfigurationInfo);
                    });

            // カスタムコマンドリスナーを解析
            allClasses.stream()
                    .filter(x -> x.getDeclaredAnnotation(CommandListener.class) != null)
                    .forEach(x -> {
                        if (CommandBeforeListener.class.isAssignableFrom(x)) {
                            CommandListenerHolder.getInstance()
                                    .addCommandBeforeListener((Class<CommandBeforeListener>) x);
                        } else if (CommandAfterListener.class.isAssignableFrom(x)) {
                            CommandListenerHolder.getInstance()
                                    .addCommandAfterListener((Class<CommandAfterListener>) x);
                        } else if (CommandErrorListener.class.isAssignableFrom(x)) {
                            CommandListenerHolder.getInstance()
                                    .addCommandErrorListener((Class<CommandErrorListener>) x);
                        }
                    });

            // >>他機能のカスタムがあれば随時追加<<

            log.debug("end parse custom function packages -> {}:{}", entry.getKey(), entry.getValue());
        }

    }

}
