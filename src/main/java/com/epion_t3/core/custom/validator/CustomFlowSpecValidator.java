package com.epion_t3.core.custom.validator;

import com.epion_t3.core.common.bean.*;
import com.epion_t3.core.common.context.Context;
import com.epion_t3.core.common.context.ExecuteContext;
import com.epion_t3.core.common.type.NotificationType;
import com.epion_t3.core.common.type.StructureType;
import com.epion_t3.core.custom.holder.CustomPackageHolder;
import com.epion_t3.core.message.MessageManager;
import com.epion_t3.core.message.impl.CoreMessages;
import com.epion_t3.core.scenario.bean.CommandSpecStructureValidateError;
import com.epion_t3.core.scenario.bean.CommandSpecValidateError;
import com.epion_t3.core.scenario.bean.FlowSpecStructureValidateError;
import com.epion_t3.core.scenario.bean.FlowSpecValidateError;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * カスタムFlowの設計検証処理.
 * 設計の検証処理が存在する目的は、カスタム設定を実装されたものが設計通り（＝YAMLの定義通り）かを
 * 実行時にも再確認することによってテスト実行時の無駄なトラブルを抑制すること。
 * 3rdParty製のカスタム機能が増えるかもしれないと楽観的に考えて、入れている機能...
 *
 * @author takashno
 */
@Slf4j
public final class CustomFlowSpecValidator {

    /**
     * シングルトンインスタンス.
     */
    private static final CustomFlowSpecValidator instance = new CustomFlowSpecValidator();

    /**
     * プライベートコンストラクタ.
     */
    private CustomFlowSpecValidator() {
        // Do Nothing...
    }

    /**
     * シングルトンインスタンスを取得.
     *
     * @return シングルトンインスタンス
     */
    public static CustomFlowSpecValidator getInstance() {
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
     * @param flowInfo       Flow情報
     * @return 検証結果（エラーのみ）
     */
    public List<FlowSpecValidateError> validateCommandSpec(
            final Context context, final ExecuteContext executeContext, String customName, final FlowInfo flowInfo) {

        log.debug("validate flow spec start {}.{}", customName, flowInfo.getId());

        List<FlowSpecValidateError> result = new ArrayList<>();

        FlowSpecInfo customFlowSpec =
                CustomPackageHolder.getInstance().getCustomFlowSpec(customName, flowInfo.getId());

        if (customFlowSpec == null) {
            executeContext.addNotification(ET3Notification.builder()
                    .stage(executeContext.getStage())
                    .level(NotificationType.ERROR)
                    .message(MessageManager.getInstance()
                            .getMessage(CoreMessages.CORE_ERR_0048, customName, flowInfo.getId()))
                    .build());
            return result;
        }

        // ここで初めてFlowIDとFlowモデルクラスの付き合わせが行えるため、
        // このタイミングでCustomPackageHolderへ登録を行う.
        CustomPackageHolder.getInstance().addCustomFlowSpec(flowInfo.getModel(), customFlowSpec);

        for (FlowSpecStructure fss : customFlowSpec.getStructures().values()) {
            try {
                // フィールドの取得
                Class.forName(flowInfo.getModel().getName());
                Field structure = getFieldFromClass(flowInfo.getModel(), fss.getName());

                // 設計定義の型
                StructureType type = StructureType.valueOfByValue(fss.getType());

                // 実際の実装型
                Class implType = structure.getType();

                switch (type) {
                    case STRING:
                        if (!String.class.isAssignableFrom(implType)) {
                            result.add(FlowSpecStructureValidateError
                                    .flowSpecStructureValidateErrorBuilder()
                                    .stage(executeContext.getStage())
                                    .level(NotificationType.ERROR)
                                    .customName(customName)
                                    .structureName(fss.getName())
                                    .message(MessageManager.getInstance().getMessage(
                                            CoreMessages.CORE_ERR_0050, customName, flowInfo.getId(), fss.getName()
                                    )).build());
                        }
                        break;
                    case NUMBER:
                        if (!Number.class.isAssignableFrom(implType)) {
                            result.add(FlowSpecStructureValidateError
                                    .flowSpecStructureValidateErrorBuilder()
                                    .stage(executeContext.getStage())
                                    .level(NotificationType.ERROR)
                                    .customName(customName)
                                    .structureName(fss.getName())
                                    .message(MessageManager.getInstance().getMessage(
                                            CoreMessages.CORE_ERR_0051, customName, flowInfo.getId(), fss.getName()
                                    )).build());
                        }
                        break;
                    case BOOLEAN:
                        if (!(Boolean.class.isAssignableFrom(implType) || boolean.class.isAssignableFrom(implType))) {
                            result.add(FlowSpecStructureValidateError
                                    .flowSpecStructureValidateErrorBuilder()
                                    .stage(executeContext.getStage())
                                    .level(NotificationType.ERROR)
                                    .customName(customName)
                                    .structureName(fss.getName())
                                    .message(MessageManager.getInstance().getMessage(
                                            CoreMessages.CORE_ERR_0052, customName, flowInfo.getId(), fss.getName()
                                    )).build());
                        }
                        break;
                    case ARRAY:
                        if (!implType.isArray() && !List.class.isAssignableFrom(implType)) {
                            result.add(FlowSpecStructureValidateError
                                    .flowSpecStructureValidateErrorBuilder()
                                    .stage(executeContext.getStage())
                                    .level(NotificationType.ERROR)
                                    .customName(customName)
                                    .structureName(fss.getName())
                                    .message(MessageManager.getInstance().getMessage(
                                            CoreMessages.CORE_ERR_0053, customName, flowInfo.getId(), fss.getName()
                                    )).build());
                        } else {
                            Type genericType = structure.getGenericType();
                            Type genericActualType = ((ParameterizedType) genericType)
                                    .getActualTypeArguments()[0];

                            // 要素型のチェック
                            validateFlowSpecRecursive(
                                    context,
                                    executeContext,
                                    customName,
                                    flowInfo,
                                    result,
                                    (Class) genericActualType,
                                    fss.getProperty(),
                                    fss.getName());
                        }
                        break;
                    case MAP:
                        if (!Map.class.isAssignableFrom(implType)) {
                            result.add(FlowSpecStructureValidateError
                                    .flowSpecStructureValidateErrorBuilder()
                                    .stage(executeContext.getStage())
                                    .level(NotificationType.ERROR)
                                    .customName(customName)
                                    .structureName(fss.getName())
                                    .message(MessageManager.getInstance().getMessage(
                                            CoreMessages.CORE_ERR_0055, customName, flowInfo.getId(), fss.getName()
                                    )).build());
                        }
                        break;
                    case OBJECT:
                        if (ClassUtils.isPrimitiveOrWrapper(implType)) {
                            result.add(FlowSpecStructureValidateError
                                    .flowSpecStructureValidateErrorBuilder()
                                    .stage(executeContext.getStage())
                                    .level(NotificationType.ERROR)
                                    .customName(customName)
                                    .structureName(fss.getName())
                                    .message(MessageManager.getInstance().getMessage(
                                            CoreMessages.CORE_ERR_0054, customName, flowInfo.getId(), fss.getName()
                                    )).build());
                        } else {
                            validateFlowSpecRecursive(
                                    context,
                                    executeContext,
                                    customName,
                                    flowInfo,
                                    result, implType,
                                    fss.getProperty(),
                                    fss.getName());
                        }
                        break;
                    default:
                        break;
                }


            } catch (NoSuchFieldException e) {
                result.add(FlowSpecStructureValidateError
                        .flowSpecStructureValidateErrorBuilder()
                        .stage(executeContext.getStage())
                        .level(NotificationType.ERROR)
                        .error(e)
                        .customName(customName)
                        .structureName(fss.getName())
                        .message(MessageManager.getInstance().getMessage(
                                CoreMessages.CORE_ERR_0057, customName, flowInfo.getId(), fss.getName())).build());
            } catch (ClassNotFoundException e) {
                result.add(FlowSpecStructureValidateError
                        .flowSpecStructureValidateErrorBuilder()
                        .stage(executeContext.getStage())
                        .level(NotificationType.ERROR)
                        .error(e)
                        .customName(customName)
                        .structureName(fss.getName())
                        .message(MessageManager.getInstance().getMessage(
                                CoreMessages.CORE_ERR_0049, customName, flowInfo.getId())).build());
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
     * @param flowInfo       Flow情報
     * @param result         結果
     * @param clazz          対象クラス
     * @param properties     構成リスト
     * @param parentPath     ネストパス
     */
    private void validateFlowSpecRecursive(Context context,
                                           ExecuteContext executeContext,
                                           String customName, final
                                           FlowInfo flowInfo,
                                           List<FlowSpecValidateError> result,
                                           Class clazz,
                                           List<FlowSpecStructure> properties,
                                           String parentPath) {
        for (FlowSpecStructure property : properties) {

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
                            result.add(FlowSpecStructureValidateError
                                    .flowSpecStructureValidateErrorBuilder()
                                    .stage(executeContext.getStage())
                                    .level(NotificationType.ERROR)
                                    .customName(customName)
                                    .structureName(property.getName())
                                    .message(MessageManager.getInstance().getMessage(
                                            CoreMessages.CORE_ERR_0050, customName, flowInfo.getId(), nowPath
                                    )).build());
                        }
                        break;
                    case NUMBER:
                        if (!Number.class.isAssignableFrom(implType)) {
                            result.add(FlowSpecStructureValidateError
                                    .flowSpecStructureValidateErrorBuilder()
                                    .stage(executeContext.getStage())
                                    .level(NotificationType.ERROR)
                                    .customName(customName)
                                    .structureName(property.getName())
                                    .message(MessageManager.getInstance().getMessage(
                                            CoreMessages.CORE_ERR_0051, customName, flowInfo.getId(), nowPath
                                    )).build());
                        }
                        break;
                    case BOOLEAN:
                        if (!(Boolean.class.isAssignableFrom(implType) || boolean.class.isAssignableFrom(implType))) {
                            result.add(FlowSpecStructureValidateError
                                    .flowSpecStructureValidateErrorBuilder()
                                    .stage(executeContext.getStage())
                                    .level(NotificationType.ERROR)
                                    .customName(customName)
                                    .structureName(property.getName())
                                    .message(MessageManager.getInstance().getMessage(
                                            CoreMessages.CORE_ERR_0052, customName, flowInfo.getId(), nowPath
                                    )).build());
                        }
                        break;
                    case ARRAY:
                        if (!implType.isArray() && !List.class.isAssignableFrom(implType)) {
                            result.add(FlowSpecStructureValidateError
                                    .flowSpecStructureValidateErrorBuilder()
                                    .stage(executeContext.getStage())
                                    .level(NotificationType.ERROR)
                                    .customName(customName)
                                    .structureName(property.getName())
                                    .message(MessageManager.getInstance().getMessage(
                                            CoreMessages.CORE_ERR_0053, customName, flowInfo.getId(), nowPath
                                    )).build());
                        } else {
                            Type genericType = structure.getGenericType();
                            Type genericActualType = ((ParameterizedType) genericType)
                                    .getActualTypeArguments()[0];

                            // 要素型のチェック
                            validateFlowSpecRecursive(
                                    context,
                                    executeContext,
                                    customName,
                                    flowInfo,
                                    result,
                                    (Class) genericActualType,
                                    property.getProperty(),
                                    property.getName());
                        }
                        break;
                    case MAP:
                        if (!Map.class.isAssignableFrom(implType)) {
                            result.add(FlowSpecStructureValidateError
                                    .flowSpecStructureValidateErrorBuilder()
                                    .stage(executeContext.getStage())
                                    .level(NotificationType.ERROR)
                                    .customName(customName)
                                    .structureName(property.getName())
                                    .message(MessageManager.getInstance().getMessage(
                                            CoreMessages.CORE_ERR_0055, customName, flowInfo.getId(), nowPath
                                    )).build());
                        }
                        break;
                    case OBJECT:
                        if (ClassUtils.isPrimitiveOrWrapper(implType)) {
                            result.add(FlowSpecStructureValidateError
                                    .flowSpecStructureValidateErrorBuilder()
                                    .stage(executeContext.getStage())
                                    .level(NotificationType.ERROR)
                                    .customName(customName)
                                    .structureName(property.getName())
                                    .message(MessageManager.getInstance().getMessage(
                                            CoreMessages.CORE_ERR_0054, customName, flowInfo.getId(), nowPath
                                    )).build());
                        } else {
                            validateFlowSpecRecursive(
                                    context,
                                    executeContext,
                                    customName,
                                    flowInfo,
                                    result, implType, property.getProperty(),
                                    nowPath);
                        }
                        break;
                    default:
                        break;
                }


            } catch (NoSuchFieldException e) {
                result.add(FlowSpecStructureValidateError
                        .flowSpecStructureValidateErrorBuilder()
                        .stage(executeContext.getStage())
                        .level(NotificationType.ERROR)
                        .error(e)
                        .customName(customName)
                        .structureName(property.getName())
                        .message(MessageManager.getInstance().getMessage(
                                CoreMessages.CORE_ERR_0057, customName, flowInfo.getId(),
                                nowPath)).build());
            } catch (ClassNotFoundException e) {
                result.add(FlowSpecStructureValidateError
                        .flowSpecStructureValidateErrorBuilder()
                        .stage(executeContext.getStage())
                        .level(NotificationType.ERROR)
                        .error(e)
                        .customName(customName)
                        .structureName(property.getName())
                        .message(MessageManager.getInstance().getMessage(
                                CoreMessages.CORE_ERR_0049, customName, flowInfo.getId())).build());
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
