package com.epion_t3.core.custom.validator;

import com.epion_t3.core.common.bean.CommandSpecInfo;
import com.epion_t3.core.common.bean.CommandSpecStructure;
import com.epion_t3.core.common.bean.ExecuteFlow;
import com.epion_t3.core.common.bean.ExecuteScenario;
import com.epion_t3.core.common.bean.scenario.Command;
import com.epion_t3.core.common.context.Context;
import com.epion_t3.core.common.context.ExecuteContext;
import com.epion_t3.core.common.type.NotificationType;
import com.epion_t3.core.common.type.StructureType;
import com.epion_t3.core.custom.holder.CustomPackageHolder;
import com.epion_t3.core.message.MessageManager;
import com.epion_t3.core.message.impl.CoreMessages;
import com.epion_t3.core.scenario.bean.CommandValidateError;
import org.apache.commons.beanutils.PropertyUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * コマンド検証処理.
 *
 * @author takashno
 */
public class CommandValidator {

    /**
     * シングルトンインスタンス.
     */
    private static final CommandValidator instance = new CommandValidator();

    /**
     * プライベートコンストラクタ.
     */
    private CommandValidator() {
        // Do Nothing...
    }

    /**
     * シングルトンインスタンスを取得.
     *
     * @return シングルトンインスタンス.
     */
    public static CommandValidator getInstance() {
        return instance;
    }

    /**
     * 検証処理.
     *
     * @param context        コンテキスト
     * @param executeContext 実行コンテキスト
     * @param command        コマンド
     * @return 検証結果
     */
    public List<CommandValidateError> validate(
            final Context context,
            final ExecuteContext executeContext,
            final ExecuteScenario executeScenario,
            final ExecuteFlow executeFlow,
            final Command command) {

        List<CommandValidateError> result = new ArrayList<>();

        CommandSpecInfo commandSpecInfo =
                CustomPackageHolder.getInstance().getCustomCommandSpec(command.getClass());

        for (CommandSpecStructure css : commandSpecInfo.getStructures().values()) {
            try {

                // 値の取得
                Object propertyValue = PropertyUtils.getProperty(command, css.getName());

                // 必須チェック
                if (css.getRequired() && propertyValue == null) {
                    result.add(CommandValidateError
                            .commandValidateErrorBuilder()
                            .stage(executeContext.getStage())
                            .level(NotificationType.ERROR)
                            .message(MessageManager.getInstance().getMessage(
                                    CoreMessages.CORE_ERR_0042,
                                    executeFlow.getFlow().getId(),
                                    command.getCommand(),
                                    css.getName()))
                            .commandId(command.getId())
                            .flowId(executeFlow.getFlow().getId())
                            .build());
                    continue;
                }

                // 設計定義の型
                StructureType type = StructureType.valueOfByValue(css.getType());

                switch (type) {

                    case STRING:
                        String stringValue = String.class.cast(propertyValue);

                        // 必須チェック（Stringの場合はEmptyもチェック）
                        if (css.getRequired() && stringValue.isEmpty()) {
                            result.add(CommandValidateError
                                    .commandValidateErrorBuilder()
                                    .stage(executeContext.getStage())
                                    .level(NotificationType.ERROR)
                                    .message(MessageManager.getInstance().getMessage(
                                            CoreMessages.CORE_ERR_0042,
                                            executeFlow.getFlow().getId(),
                                            command.getCommand(),
                                            css.getName()))
                                    .commandId(command.getId())
                                    .flowId(executeFlow.getFlow().getId())
                                    .build());
                            continue;
                        }

                        // パターンチェック
                        if (css.getPattern() != null && !css.getPattern().isEmpty()) {
                            Pattern checkPattern = Pattern.compile(css.getPattern());
                            Matcher checkMatcher = checkPattern.matcher(stringValue);
                            if (!checkMatcher.matches()) {
                                result.add(CommandValidateError
                                        .commandValidateErrorBuilder()
                                        .stage(executeContext.getStage())
                                        .level(NotificationType.ERROR)
                                        .message(MessageManager.getInstance().getMessage(
                                                CoreMessages.CORE_ERR_0043,
                                                executeFlow.getFlow().getId(),
                                                command.getCommand(),
                                                css.getName(), css.getPattern()))
                                        .commandId(command.getId())
                                        .flowId(executeFlow.getFlow().getId())
                                        .build());
                                continue;
                            }
                        }
                        break;
                    case ARRAY:
                        if (propertyValue.getClass().isArray()) {
                            // 配列型である場合
                            Object[] array = (Object[]) propertyValue;
                            for (Object element : array) {
                                validateRecursive(
                                        context,
                                        executeContext,
                                        executeScenario,
                                        executeFlow,
                                        command,
                                        result,
                                        element,
                                        css.getProperty(),
                                        css.getName());
                            }
                        } else if (List.class.isAssignableFrom(propertyValue.getClass())) {
                            // List型である場合
                            List list = (List) propertyValue;
                            for (Object element : list) {
                                validateRecursive(
                                        context,
                                        executeContext,
                                        executeScenario,
                                        executeFlow,
                                        command,
                                        result,
                                        element,
                                        css.getProperty(),
                                        css.getName());
                            }
                        }
                        break;
                    case OBJECT:
                        validateRecursive(
                                context,
                                executeContext,
                                executeScenario,
                                executeFlow,
                                command,
                                result,
                                propertyValue,
                                css.getProperty(),
                                css.getName());
                        break;
                    default:
                        // Do Nothing...
                        break;
                }

            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                result.add(CommandValidateError
                        .commandValidateErrorBuilder()
                        .stage(executeContext.getStage())
                        .level(NotificationType.ERROR)
                        .error(e)
                        .message(MessageManager.getInstance().getMessage(
                                CoreMessages.CORE_ERR_0044,
                                executeFlow.getFlow().getId(),
                                command.getCommand(),
                                css.getName()))
                        .commandId(command.getId())
                        .flowId(executeFlow.getFlow().getId())
                        .build());
            }
        }
        return result;
    }

    /**
     * 再帰的にコマンド値の検証を行う.
     *
     * @param context        コンテキスト
     * @param executeContext 実行コンテキスト
     * @param command        コマンド
     * @param result         検証結果
     * @param obj            対象オブジェクト
     * @param properties     検証プロパティリスト
     * @param parentPath     ネストパス
     */
    private void validateRecursive(
            final Context context,
            final ExecuteContext executeContext,
            final ExecuteScenario executeScenario,
            final ExecuteFlow executeFlow,
            final Command command,
            final List<CommandValidateError> result,
            final Object obj,
            final List<CommandSpecStructure> properties,
            String parentPath) {

        for (CommandSpecStructure css : properties) {

            String nowPath = parentPath + "." + css.getName();

            try {

                // 値の取得
                Object propertyValue = PropertyUtils.getProperty(obj, css.getName());

                // 必須チェック
                if (css.getRequired() && propertyValue == null) {
                    result.add(CommandValidateError
                            .commandValidateErrorBuilder()
                            .stage(executeContext.getStage())
                            .level(NotificationType.ERROR)
                            .message(MessageManager.getInstance().getMessage(
                                    CoreMessages.CORE_ERR_0042,
                                    executeFlow.getFlow().getId(),
                                    command.getCommand(),
                                    nowPath))
                            .commandId(command.getId())
                            .flowId(executeFlow.getFlow().getId())
                            .build());
                    continue;
                } else if (!css.getRequired() && propertyValue == null) {
                    // NULLを許容するが、後続処理を行う必要がないため次のプロパティへ
                    continue;
                }

                // 設計定義の型
                StructureType type = StructureType.valueOfByValue(css.getType());

                switch (type) {

                    case STRING:
                        String stringValue = String.class.cast(propertyValue);

                        // 必須チェック（Stringの場合はEmptyもチェック）
                        if (css.getRequired() && stringValue.isEmpty()) {
                            result.add(CommandValidateError
                                    .commandValidateErrorBuilder()
                                    .stage(executeContext.getStage())
                                    .level(NotificationType.ERROR)
                                    .message(MessageManager.getInstance().getMessage(
                                            CoreMessages.CORE_ERR_0042,
                                            executeFlow.getFlow().getId(),
                                            command.getCommand(),
                                            nowPath))
                                    .commandId(command.getId())
                                    .flowId(executeFlow.getFlow().getId())
                                    .build());
                            continue;
                        }

                        // パターンチェック
                        if (css.getPattern() != null && !css.getPattern().isEmpty()) {
                            Pattern checkPattern = Pattern.compile(css.getPattern());
                            Matcher checkMatcher = checkPattern.matcher(stringValue);
                            if (!checkMatcher.matches()) {
                                result.add(CommandValidateError
                                        .commandValidateErrorBuilder()
                                        .stage(executeContext.getStage())
                                        .level(NotificationType.ERROR)
                                        .message(MessageManager.getInstance().getMessage(
                                                CoreMessages.CORE_ERR_0043,
                                                executeFlow.getFlow().getId(),
                                                command.getCommand(),
                                                nowPath,
                                                css.getPattern()))
                                        .commandId(command.getId())
                                        .flowId(executeFlow.getFlow().getId())
                                        .build());
                                continue;
                            }
                        }
                        break;
                    case ARRAY:
                        if (propertyValue.getClass().isArray()) {
                            // 配列型である場合
                            Object[] array = (Object[]) propertyValue;
                            for (Object element : array) {
                                validateRecursive(
                                        context,
                                        executeContext,
                                        executeScenario,
                                        executeFlow,
                                        command,
                                        result,
                                        element,
                                        css.getProperty(),
                                        nowPath);
                            }
                        } else if (List.class.isAssignableFrom(propertyValue.getClass())) {
                            // List型である場合
                            List list = (List) propertyValue;
                            for (Object element : list) {
                                validateRecursive(
                                        context,
                                        executeContext,
                                        executeScenario,
                                        executeFlow,
                                        command,
                                        result,
                                        element,
                                        css.getProperty(),
                                        nowPath);
                            }
                        }
                        break;
                    case OBJECT:
                        // Object型である場合
                        validateRecursive(
                                context,
                                executeContext,
                                executeScenario,
                                executeFlow,
                                command,
                                result,
                                propertyValue,
                                css.getProperty(),
                                nowPath);
                        break;
                    default:
                        // Do Nothing...
                        break;
                }

            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                result.add(CommandValidateError
                        .commandValidateErrorBuilder()
                        .stage(executeContext.getStage())
                        .level(NotificationType.ERROR)
                        .error(e)
                        .message(MessageManager.getInstance().getMessage(
                                CoreMessages.CORE_ERR_0044,
                                executeFlow.getFlow().getId(),
                                command.getCommand(),
                                css.getName()))
                        .commandId(command.getId())
                        .flowId(executeFlow.getFlow().getId())
                        .build());
            }
        }


    }
}
