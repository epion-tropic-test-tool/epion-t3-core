/* Copyright (c) 2017-2019 Nozomu Takashima. */
package com.epion_t3.core.custom.validator;

import com.epion_t3.core.common.bean.CustomConfigurationInfo;
import com.epion_t3.core.common.bean.CustomConfigurationSpecInfo;
import com.epion_t3.core.common.bean.CustomConfigurationSpecStructure;
import com.epion_t3.core.common.bean.ET3Notification;
import com.epion_t3.core.common.context.Context;
import com.epion_t3.core.common.context.ExecuteContext;
import com.epion_t3.core.common.type.NotificationType;
import com.epion_t3.core.common.type.StructureType;
import com.epion_t3.core.custom.holder.CustomPackageHolder;
import com.epion_t3.core.message.MessageManager;
import com.epion_t3.core.message.impl.CoreMessages;
import com.epion_t3.core.scenario.bean.ConfigurationSpecStructureValidateError;
import com.epion_t3.core.scenario.bean.ConfigurationSpecValidateError;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * カスタム設定定義の設計検証処理. 設計の検証処理が存在する目的は、カスタム設定定義を実装されたものが設計通り（＝YAMLの定義通り）かを
 * 実行時にも再確認することによってテスト実行時の無駄なトラブルを抑制すること。
 * 3rdParty製のカスタム機能が増えるかもしれないと楽観的に考えて、入れている機能...
 *
 * @author takashno
 */
@Slf4j
public final class CustomConfigurationSpecValidator {

    /**
     * シングルトンインスタンス.
     */
    private static final CustomConfigurationSpecValidator instance = new CustomConfigurationSpecValidator();

    /**
     * プライベートコンストラクタ.
     */
    private CustomConfigurationSpecValidator() {
        // Do Nothing...
    }

    /**
     * シングルトンインスタンスを取得.
     *
     * @return シングルトンインスタンス
     */
    public static CustomConfigurationSpecValidator getInstance() {
        return instance;
    }

    /**
     * カスタム設定意義の設計と実装の検証を行う. この検証の意図は、設計＝ユーザーが頼りにする情報と実装に相違がある場合、解析にとても時間がかかる.
     * ユーザビリティの観点から、ツールとしても設計と実装の生合成がある程度取れていることを確認するため.
     *
     * @param context コンテキスト
     * @param executeContext 実行コンテキスト
     * @param customName カスタム名
     * @param customConfigurationInfo コマンド情報
     * @return 検証結果（エラーのみ）
     */
    public List<ConfigurationSpecValidateError> validateConfigurationSpec(final Context context,
            final ExecuteContext executeContext, String customName,
            final CustomConfigurationInfo customConfigurationInfo) {

        log.debug("validate configuration spec start {}.{}", customName, customConfigurationInfo.getId());

        List<ConfigurationSpecValidateError> result = new ArrayList<>();

        CustomConfigurationSpecInfo customConfigurationSpecInfo = CustomPackageHolder.getInstance()
                .getCustomConfigurationSpec(customName, customConfigurationInfo.getId());

        if (customConfigurationSpecInfo == null) {
            executeContext.addNotification(ET3Notification.builder()
                    .stage(executeContext.getStage())
                    .level(NotificationType.ERROR)
                    .message(MessageManager.getInstance()
                            .getMessage(CoreMessages.CORE_ERR_0047, customName, customConfigurationInfo.getId()))
                    .build());
            return result;
        }

        // ここで初めてコマンドIDとコマンドモデルクラスの付き合わせが行えるため、
        // このタイミングでCustomPackageHolderへ登録を行う.
        CustomPackageHolder.getInstance()
                .addCustomConfigurationSpec(customConfigurationInfo.getModel(), customConfigurationSpecInfo);

        for (CustomConfigurationSpecStructure ccss : customConfigurationSpecInfo.getStructures().values()) {
            try {
                // フィールドの取得
                Class.forName(customConfigurationInfo.getModel().getName());
                Field structure = getFieldFromClass(customConfigurationInfo.getModel(), ccss.getName());

                // 設計定義の型
                StructureType type = StructureType.valueOfByValue(ccss.getType());

                // 実際の実装型
                Class implType = structure.getType();

                switch (type) {
                case STRING:
                    if (!String.class.isAssignableFrom(implType)) {
                        result.add(
                                ConfigurationSpecStructureValidateError.configurationSpecStructureValidateErrorBuilder()
                                        .stage(executeContext.getStage())
                                        .level(NotificationType.ERROR)
                                        .customName(customName)
                                        .structureName(ccss.getName())
                                        .message(MessageManager.getInstance()
                                                .getMessage(CoreMessages.CORE_ERR_0058, customName,
                                                        customConfigurationInfo.getId(), ccss.getName()))
                                        .build());
                    }
                    break;
                case NUMBER:
                    if (!Number.class.isAssignableFrom(implType)) {
                        result.add(
                                ConfigurationSpecStructureValidateError.configurationSpecStructureValidateErrorBuilder()
                                        .stage(executeContext.getStage())
                                        .level(NotificationType.ERROR)
                                        .customName(customName)
                                        .structureName(ccss.getName())
                                        .message(MessageManager.getInstance()
                                                .getMessage(CoreMessages.CORE_ERR_0059, customName,
                                                        customConfigurationInfo.getId(), ccss.getName()))
                                        .build());
                    }
                    break;
                case BOOLEAN:
                    if (!(Boolean.class.isAssignableFrom(implType) || boolean.class.isAssignableFrom(implType))) {
                        result.add(
                                ConfigurationSpecStructureValidateError.configurationSpecStructureValidateErrorBuilder()
                                        .stage(executeContext.getStage())
                                        .level(NotificationType.ERROR)
                                        .customName(customName)
                                        .structureName(ccss.getName())
                                        .message(MessageManager.getInstance()
                                                .getMessage(CoreMessages.CORE_ERR_0060, customName,
                                                        customConfigurationInfo.getId(), ccss.getName()))
                                        .build());
                    }
                    break;
                case ARRAY:
                    if (!implType.isArray() && !List.class.isAssignableFrom(implType)) {
                        result.add(
                                ConfigurationSpecStructureValidateError.configurationSpecStructureValidateErrorBuilder()
                                        .stage(executeContext.getStage())
                                        .level(NotificationType.ERROR)
                                        .customName(customName)
                                        .structureName(ccss.getName())
                                        .message(MessageManager.getInstance()
                                                .getMessage(CoreMessages.CORE_ERR_0061, customName,
                                                        customConfigurationInfo.getId(), ccss.getName()))
                                        .build());
                    } else {
                        Type genericType = structure.getGenericType();
                        Type genericActualType = ((ParameterizedType) genericType).getActualTypeArguments()[0];

                        // 要素型のチェック
                        validateCustomConfigurationSpecRecursive(context, executeContext, customName,
                                customConfigurationInfo, result, (Class) genericActualType, ccss.getProperty(),
                                ccss.getName());
                    }
                    break;
                case MAP:
                    if (!Map.class.isAssignableFrom(implType)) {
                        result.add(
                                ConfigurationSpecStructureValidateError.configurationSpecStructureValidateErrorBuilder()
                                        .stage(executeContext.getStage())
                                        .level(NotificationType.ERROR)
                                        .customName(customName)
                                        .structureName(ccss.getName())
                                        .message(MessageManager.getInstance()
                                                .getMessage(CoreMessages.CORE_ERR_0063, customName,
                                                        customConfigurationInfo.getId(), ccss.getName()))
                                        .build());
                    }
                    break;
                case OBJECT:
                    if (ClassUtils.isPrimitiveOrWrapper(implType)) {
                        result.add(
                                ConfigurationSpecStructureValidateError.configurationSpecStructureValidateErrorBuilder()
                                        .stage(executeContext.getStage())
                                        .level(NotificationType.ERROR)
                                        .customName(customName)
                                        .structureName(ccss.getName())
                                        .message(MessageManager.getInstance()
                                                .getMessage(CoreMessages.CORE_ERR_0062, customName,
                                                        customConfigurationInfo.getId(), ccss.getName()))
                                        .build());
                    } else {
                        validateCustomConfigurationSpecRecursive(context, executeContext, customName,
                                customConfigurationInfo, result, implType, ccss.getProperty(), ccss.getName());
                    }
                    break;
                default:
                    break;
                }

            } catch (NoSuchFieldException e) {
                result.add(ConfigurationSpecStructureValidateError.configurationSpecStructureValidateErrorBuilder()
                        .stage(executeContext.getStage())
                        .level(NotificationType.ERROR)
                        .error(e)
                        .customName(customName)
                        .structureName(ccss.getName())
                        .message(MessageManager.getInstance()
                                .getMessage(CoreMessages.CORE_ERR_0064, customName, customConfigurationInfo.getId(),
                                        ccss.getName()))
                        .build());
            } catch (ClassNotFoundException e) {
                result.add(ConfigurationSpecStructureValidateError.configurationSpecStructureValidateErrorBuilder()
                        .stage(executeContext.getStage())
                        .level(NotificationType.ERROR)
                        .error(e)
                        .customName(customName)
                        .structureName(ccss.getName())
                        .message(MessageManager.getInstance()
                                .getMessage(CoreMessages.CORE_ERR_0057, customName, customConfigurationInfo.getId()))
                        .build());
            }
        }
        return result;
    }

    /**
     * カスタム機能の設計と実装とのバリデーション.（再帰処理用）
     *
     * @param context コンテキスト
     * @param executeContext 実行コンテキスト
     * @param customName カスタム名
     * @param commandInfo コマンド情報
     * @param result 結果
     * @param clazz 対象クラス
     * @param properties 構成リスト
     * @param parentPath ネストパス
     */
    private void validateCustomConfigurationSpecRecursive(Context context, ExecuteContext executeContext,
            String customName, final CustomConfigurationInfo commandInfo, List<ConfigurationSpecValidateError> result,
            Class clazz, List<CustomConfigurationSpecStructure> properties, String parentPath) {
        for (CustomConfigurationSpecStructure property : properties) {

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
                        result.add(
                                ConfigurationSpecStructureValidateError.configurationSpecStructureValidateErrorBuilder()
                                        .stage(executeContext.getStage())
                                        .level(NotificationType.ERROR)
                                        .customName(customName)
                                        .structureName(property.getName())
                                        .message(MessageManager.getInstance()
                                                .getMessage(CoreMessages.CORE_ERR_0058, customName, commandInfo.getId(),
                                                        nowPath))
                                        .build());
                    }
                    break;
                case NUMBER:
                    if (!Number.class.isAssignableFrom(implType)) {
                        result.add(
                                ConfigurationSpecStructureValidateError.configurationSpecStructureValidateErrorBuilder()
                                        .stage(executeContext.getStage())
                                        .level(NotificationType.ERROR)
                                        .customName(customName)
                                        .structureName(property.getName())
                                        .message(MessageManager.getInstance()
                                                .getMessage(CoreMessages.CORE_ERR_0059, customName, commandInfo.getId(),
                                                        nowPath))
                                        .build());
                    }
                    break;
                case BOOLEAN:
                    if (!(Boolean.class.isAssignableFrom(implType) || boolean.class.isAssignableFrom(implType))) {
                        result.add(
                                ConfigurationSpecStructureValidateError.configurationSpecStructureValidateErrorBuilder()
                                        .stage(executeContext.getStage())
                                        .level(NotificationType.ERROR)
                                        .customName(customName)
                                        .structureName(property.getName())
                                        .message(MessageManager.getInstance()
                                                .getMessage(CoreMessages.CORE_ERR_0060, customName, commandInfo.getId(),
                                                        nowPath))
                                        .build());
                    }
                    break;
                case ARRAY:
                    if (!implType.isArray() && !List.class.isAssignableFrom(implType)) {
                        result.add(
                                ConfigurationSpecStructureValidateError.configurationSpecStructureValidateErrorBuilder()
                                        .stage(executeContext.getStage())
                                        .level(NotificationType.ERROR)
                                        .customName(customName)
                                        .structureName(property.getName())
                                        .message(MessageManager.getInstance()
                                                .getMessage(CoreMessages.CORE_ERR_0061, customName, commandInfo.getId(),
                                                        nowPath))
                                        .build());
                    } else {
                        Type genericType = structure.getGenericType();
                        Type genericActualType = ((ParameterizedType) genericType).getActualTypeArguments()[0];

                        // 要素型のチェック
                        validateCustomConfigurationSpecRecursive(context, executeContext, customName, commandInfo,
                                result, (Class) genericActualType, property.getProperty(), property.getName());
                    }
                    break;
                case MAP:
                    if (!Map.class.isAssignableFrom(implType)) {
                        result.add(
                                ConfigurationSpecStructureValidateError.configurationSpecStructureValidateErrorBuilder()
                                        .stage(executeContext.getStage())
                                        .level(NotificationType.ERROR)
                                        .customName(customName)
                                        .structureName(property.getName())
                                        .message(MessageManager.getInstance()
                                                .getMessage(CoreMessages.CORE_ERR_0063, customName, commandInfo.getId(),
                                                        nowPath))
                                        .build());
                    }
                    break;
                case OBJECT:
                    if (ClassUtils.isPrimitiveOrWrapper(implType)) {
                        result.add(
                                ConfigurationSpecStructureValidateError.configurationSpecStructureValidateErrorBuilder()
                                        .stage(executeContext.getStage())
                                        .level(NotificationType.ERROR)
                                        .customName(customName)
                                        .structureName(property.getName())
                                        .message(MessageManager.getInstance()
                                                .getMessage(CoreMessages.CORE_ERR_0062, customName, commandInfo.getId(),
                                                        nowPath))
                                        .build());
                    } else {
                        validateCustomConfigurationSpecRecursive(context, executeContext, customName, commandInfo,
                                result, implType, property.getProperty(), nowPath);
                    }
                    break;
                default:
                    break;
                }

            } catch (NoSuchFieldException e) {
                result.add(ConfigurationSpecStructureValidateError.configurationSpecStructureValidateErrorBuilder()
                        .stage(executeContext.getStage())
                        .level(NotificationType.ERROR)
                        .error(e)
                        .customName(customName)
                        .structureName(property.getName())
                        .message(MessageManager.getInstance()
                                .getMessage(CoreMessages.CORE_ERR_0064, customName, commandInfo.getId(), nowPath))
                        .build());
            } catch (ClassNotFoundException e) {
                result.add(ConfigurationSpecStructureValidateError.configurationSpecStructureValidateErrorBuilder()
                        .stage(executeContext.getStage())
                        .level(NotificationType.ERROR)
                        .error(e)
                        .customName(customName)
                        .structureName(property.getName())
                        .message(MessageManager.getInstance()
                                .getMessage(CoreMessages.CORE_ERR_0057, customName, commandInfo.getId()))
                        .build());
            }
        }
    }

    /**
     * 親クラスを含めた全てのプロパティから指定フィールドを走査.
     *
     * @param clazz 対象クラス
     * @param fieldName フィールド名
     * @return フィールド
     * @throws NoSuchFieldException フィールドが見つからなかった場合
     */
    private Field getFieldFromClass(Class clazz, String fieldName) throws NoSuchFieldException {
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
