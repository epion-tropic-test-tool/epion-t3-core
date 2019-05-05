package com.epion_t3.core.custom.validator;

import com.epion_t3.core.common.bean.CommandInfo;
import com.epion_t3.core.common.bean.CommandSpecInfo;
import com.epion_t3.core.common.bean.CommandSpecStructure;
import com.epion_t3.core.common.bean.ET3Notification;
import com.epion_t3.core.common.context.Context;
import com.epion_t3.core.common.context.ExecuteContext;
import com.epion_t3.core.common.type.NotificationType;
import com.epion_t3.core.common.type.StructureType;
import com.epion_t3.core.custom.holder.CustomPackageHolder;
import com.epion_t3.core.message.MessageManager;
import com.epion_t3.core.message.impl.CoreMessages;
import com.epion_t3.core.scenario.bean.CommandSpecStructureValidateError;
import com.epion_t3.core.scenario.bean.CommandSpecValidateError;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * カスタム機能の設計検証処理.
 *
 * @author takashno
 */
@Slf4j
public class CustomSpecValidator {

    /**
     * シングルトンインスタンス.
     */
    private static final CustomSpecValidator instance = new CustomSpecValidator();

    /**
     * プライベートコンストラクタ.
     */
    private CustomSpecValidator() {
        // Do Nothing...
    }

    /**
     * シングルトンインスタンスを取得.
     *
     * @return シングルトンインスタンス
     */
    public static CustomSpecValidator getInstance() {
        return instance;
    }

    /**
     * カスタムコマンドの設計と実装の検証を行う.
     * この検証の意図は、設計＝ユーザーが頼りにする情報と実装に相違がある場合、解析にとても時間がかかる.
     * ユーザビリティの観点から、ツールとしても設計と実装の生合成がある程度取れていることを確認するため.
     *
     * @param context        コンテキスト
     * @param executeContext 実行コンテキスト
     * @param customName     カスタム名
     * @param commandInfo    コマンド情報
     * @return 検証結果（エラーのみ）
     */
    public List<CommandSpecValidateError> validateCommandSpec(
            final Context context, final ExecuteContext executeContext, String customName, final CommandInfo commandInfo) {

        log.debug("validate start {}.{}", customName, commandInfo.getId());

        List<CommandSpecValidateError> result = new ArrayList<>();

        CommandSpecInfo commandSpec =
                CustomPackageHolder.getInstance().getCommandSpec(customName, commandInfo.getId());

        // ここで初めてコマンドIDとコマンドモデルクラスの付き合わせが行えるため、
        // このタイミングでCustomPackageHolderへ登録を行う.
        CustomPackageHolder.getInstance().addCustomCommandSpec(commandInfo.getModel(), commandSpec);

        if (commandSpec == null) {
            executeContext.addNotification(ET3Notification.builder()
                    .stage(executeContext.getStage())
                    .level(NotificationType.ERROR)
                    .message(MessageManager.getInstance()
                            .getMessage(CoreMessages.CORE_ERR_0038, customName, commandInfo.getId()))
                    .build());
            return result;
        }

        for (CommandSpecStructure css : commandSpec.getStructures().values()) {
            try {
                // フィールドの取得
                Class.forName(commandInfo.getModel().getName());
                Field structure = getFieldFromClass(commandInfo.getModel(), css.getName());

                // 設計定義の型
                StructureType type = StructureType.valueOfByValue(css.getType());

                // 実際の実装型
                Class implType = structure.getType();

                switch (type) {
                    case STRING:
                        if (!String.class.isAssignableFrom(implType)) {
                            result.add(CommandSpecStructureValidateError
                                    .commandSpecStructureValidateErrorBuilder()
                                    .stage(executeContext.getStage())
                                    .level(NotificationType.ERROR)
                                    .customName(customName)
                                    .structureName(css.getName())
                                    .message(MessageManager.getInstance().getMessage(
                                            CoreMessages.CORE_ERR_0026, customName, commandInfo.getId(), css.getName()
                                    )).build());
                        }
                        break;
                    case NUMBER:
                        if (!Number.class.isAssignableFrom(implType)) {
                            result.add(CommandSpecStructureValidateError
                                    .commandSpecStructureValidateErrorBuilder()
                                    .stage(executeContext.getStage())
                                    .level(NotificationType.ERROR)
                                    .customName(customName)
                                    .structureName(css.getName())
                                    .message(MessageManager.getInstance().getMessage(
                                            CoreMessages.CORE_ERR_0027, customName, commandInfo.getId(), css.getName()
                                    )).build());
                        }
                        break;
                    case BOOLEAN:
                        if (!(Boolean.class.isAssignableFrom(implType) || boolean.class.isAssignableFrom(implType))) {
                            result.add(CommandSpecStructureValidateError
                                    .commandSpecStructureValidateErrorBuilder()
                                    .stage(executeContext.getStage())
                                    .level(NotificationType.ERROR)
                                    .customName(customName)
                                    .structureName(css.getName())
                                    .message(MessageManager.getInstance().getMessage(
                                            CoreMessages.CORE_ERR_0028, customName, commandInfo.getId(), css.getName()
                                    )).build());
                        }
                        break;
                    case ARRAY:
                        if (!implType.isArray() && !List.class.isAssignableFrom(implType)) {
                            result.add(CommandSpecStructureValidateError
                                    .commandSpecStructureValidateErrorBuilder()
                                    .stage(executeContext.getStage())
                                    .level(NotificationType.ERROR)
                                    .customName(customName)
                                    .structureName(css.getName())
                                    .message(MessageManager.getInstance().getMessage(
                                            CoreMessages.CORE_ERR_0029, customName, commandInfo.getId(), css.getName()
                                    )).build());
                        } else {
                            Type genericType = structure.getGenericType();
                            Type genericActualType = ((ParameterizedType) genericType)
                                    .getActualTypeArguments()[0];

                            // 要素型のチェック
                            validateCommandSpecRecursive(
                                    context,
                                    executeContext,
                                    customName,
                                    commandInfo,
                                    result,
                                    (Class) genericActualType,
                                    css.getProperty(),
                                    css.getName());
                        }
                        break;
                    case MAP:
                        if (!Map.class.isAssignableFrom(implType)) {
                            result.add(CommandSpecStructureValidateError
                                    .commandSpecStructureValidateErrorBuilder()
                                    .stage(executeContext.getStage())
                                    .level(NotificationType.ERROR)
                                    .customName(customName)
                                    .structureName(css.getName())
                                    .message(MessageManager.getInstance().getMessage(
                                            CoreMessages.CORE_ERR_0041, customName, commandInfo.getId(), css.getName()
                                    )).build());
                        }
                        break;
                    case OBJECT:
                        if (ClassUtils.isPrimitiveOrWrapper(implType)) {
                            result.add(CommandSpecStructureValidateError
                                    .commandSpecStructureValidateErrorBuilder()
                                    .stage(executeContext.getStage())
                                    .level(NotificationType.ERROR)
                                    .customName(customName)
                                    .structureName(css.getName())
                                    .message(MessageManager.getInstance().getMessage(
                                            CoreMessages.CORE_ERR_0030, customName, commandInfo.getId(), css.getName()
                                    )).build());
                        } else {
                            validateCommandSpecRecursive(
                                    context,
                                    executeContext,
                                    customName,
                                    commandInfo,
                                    result, implType,
                                    css.getProperty(),
                                    css.getName());
                        }
                        break;
                    default:
                        break;
                }


            } catch (NoSuchFieldException e) {
                result.add(CommandSpecStructureValidateError
                        .commandSpecStructureValidateErrorBuilder()
                        .stage(executeContext.getStage())
                        .level(NotificationType.ERROR)
                        .error(e)
                        .customName(customName)
                        .structureName(css.getName())
                        .message(MessageManager.getInstance().getMessage(
                                CoreMessages.CORE_ERR_0031, customName, commandInfo.getId(), css.getName())).build());
            } catch (ClassNotFoundException e) {
                result.add(CommandSpecStructureValidateError
                        .commandSpecStructureValidateErrorBuilder()
                        .stage(executeContext.getStage())
                        .level(NotificationType.ERROR)
                        .error(e)
                        .customName(customName)
                        .structureName(css.getName())
                        .message(MessageManager.getInstance().getMessage(
                                CoreMessages.CORE_ERR_0039, customName, commandInfo.getId())).build());
            }
        }
        return result;
    }

    /**
     * カスタム機能の設計と実装とのバリデーション.（再帰処理用）
     *
     * @param context        コンテキスト
     * @param executeContext 実行コンテキスト
     * @param customName     カスタム名
     * @param commandInfo    コマンド情報
     * @param result         結果
     * @param clazz          対象クラス
     * @param properties     構成リスト
     * @param parentPath     ネストパス
     */
    private void validateCommandSpecRecursive(Context context,
                                              ExecuteContext executeContext,
                                              String customName, final
                                              CommandInfo commandInfo,
                                              List<CommandSpecValidateError> result,
                                              Class clazz,
                                              List<CommandSpecStructure> properties,
                                              String parentPath) {
        for (CommandSpecStructure property : properties) {

            String nowPath = parentPath + "." + property.getName();

            try {
                // フィールドの取得
                Class.forName(clazz.getName());
                Field structure = getFieldFromClass(clazz, property.getName());

                // 設計定義の型
                StructureType type = StructureType.valueOfByValue(property.getType());

                // 実際の実装型
                Class implType = structure.getType();


                switch (type) {
                    case STRING:
                        if (!String.class.isAssignableFrom(implType)) {
                            result.add(CommandSpecStructureValidateError
                                    .commandSpecStructureValidateErrorBuilder()
                                    .stage(executeContext.getStage())
                                    .level(NotificationType.ERROR)
                                    .customName(customName)
                                    .structureName(property.getName())
                                    .message(MessageManager.getInstance().getMessage(
                                            CoreMessages.CORE_ERR_0026, customName, commandInfo.getId(), nowPath
                                    )).build());
                        }
                        break;
                    case NUMBER:
                        if (!Number.class.isAssignableFrom(implType)) {
                            result.add(CommandSpecStructureValidateError
                                    .commandSpecStructureValidateErrorBuilder()
                                    .stage(executeContext.getStage())
                                    .level(NotificationType.ERROR)
                                    .customName(customName)
                                    .structureName(property.getName())
                                    .message(MessageManager.getInstance().getMessage(
                                            CoreMessages.CORE_ERR_0027, customName, commandInfo.getId(), nowPath
                                    )).build());
                        }
                        break;
                    case BOOLEAN:
                        if (!(Boolean.class.isAssignableFrom(implType) || boolean.class.isAssignableFrom(implType))) {
                            result.add(CommandSpecStructureValidateError
                                    .commandSpecStructureValidateErrorBuilder()
                                    .stage(executeContext.getStage())
                                    .level(NotificationType.ERROR)
                                    .customName(customName)
                                    .structureName(property.getName())
                                    .message(MessageManager.getInstance().getMessage(
                                            CoreMessages.CORE_ERR_0028, customName, commandInfo.getId(), nowPath
                                    )).build());
                        }
                        break;
                    case ARRAY:
                        if (!implType.isArray() && !List.class.isAssignableFrom(implType)) {
                            result.add(CommandSpecStructureValidateError
                                    .commandSpecStructureValidateErrorBuilder()
                                    .stage(executeContext.getStage())
                                    .level(NotificationType.ERROR)
                                    .customName(customName)
                                    .structureName(property.getName())
                                    .message(MessageManager.getInstance().getMessage(
                                            CoreMessages.CORE_ERR_0029, customName, commandInfo.getId(), nowPath
                                    )).build());
                        } else {
                            Type genericType = structure.getGenericType();
                            Type genericActualType = ((ParameterizedType) genericType)
                                    .getActualTypeArguments()[0];

                            // 要素型のチェック
                            validateCommandSpecRecursive(
                                    context,
                                    executeContext,
                                    customName,
                                    commandInfo,
                                    result,
                                    (Class) genericActualType,
                                    property.getProperty(),
                                    property.getName());
                        }
                        break;
                    case MAP:
                        if (!Map.class.isAssignableFrom(implType)) {
                            result.add(CommandSpecStructureValidateError
                                    .commandSpecStructureValidateErrorBuilder()
                                    .stage(executeContext.getStage())
                                    .level(NotificationType.ERROR)
                                    .customName(customName)
                                    .structureName(property.getName())
                                    .message(MessageManager.getInstance().getMessage(
                                            CoreMessages.CORE_ERR_0041, customName, commandInfo.getId(), nowPath
                                    )).build());
                        }
                        break;
                    case OBJECT:
                        if (ClassUtils.isPrimitiveOrWrapper(implType)) {
                            result.add(CommandSpecStructureValidateError
                                    .commandSpecStructureValidateErrorBuilder()
                                    .stage(executeContext.getStage())
                                    .level(NotificationType.ERROR)
                                    .customName(customName)
                                    .structureName(property.getName())
                                    .message(MessageManager.getInstance().getMessage(
                                            CoreMessages.CORE_ERR_0030, customName, commandInfo.getId(), nowPath
                                    )).build());
                        } else {
                            validateCommandSpecRecursive(
                                    context,
                                    executeContext,
                                    customName,
                                    commandInfo,
                                    result, implType, property.getProperty(),
                                    nowPath);
                        }
                        break;
                    default:
                        break;
                }


            } catch (NoSuchFieldException e) {
                result.add(CommandSpecStructureValidateError
                        .commandSpecStructureValidateErrorBuilder()
                        .stage(executeContext.getStage())
                        .level(NotificationType.ERROR)
                        .error(e)
                        .customName(customName)
                        .structureName(property.getName())
                        .message(MessageManager.getInstance().getMessage(
                                CoreMessages.CORE_ERR_0031, customName, commandInfo.getId(),
                                nowPath)).build());
            } catch (ClassNotFoundException e) {
                result.add(CommandSpecStructureValidateError
                        .commandSpecStructureValidateErrorBuilder()
                        .stage(executeContext.getStage())
                        .level(NotificationType.ERROR)
                        .error(e)
                        .customName(customName)
                        .structureName(property.getName())
                        .message(MessageManager.getInstance().getMessage(
                                CoreMessages.CORE_ERR_0039, customName, commandInfo.getId())).build());
            }
        }
    }

    /**
     * 親クラスを含めた全てのプロパティから指定フィールドを走査.
     *
     * @param clazz     対象クラス
     * @param fieldName フィールド名
     * @return フィールド
     * @throws NoSuchFieldException フィールドが見つからなかった場合
     */
    private Field getFieldFromClass(Class clazz, String fieldName)
            throws NoSuchFieldException {
        Field field = null;
        while (clazz != null) {
            try {
                field = clazz.getDeclaredField(fieldName);
                break;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }

        if (field == null) {
            throw new NoSuchFieldException();
        }
        return field;
    }
}
