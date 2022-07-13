/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.common.util;

import com.epion_t3.core.common.type.ReferenceVariableType;
import com.epion_t3.core.exception.SystemException;
import com.epion_t3.core.message.MessageManager;
import com.epion_t3.core.message.impl.CoreMessages;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * バインドユーティリティ.
 *
 * @author takashno
 */
@Slf4j
public final class BindUtils {

    /**
     * シングルトンインスタンス.
     */
    private static final BindUtils instance = new BindUtils();

    /**
     * バインド変数抽出パターン.
     */
    public static final Pattern BIND_EXTRACT_PATTERN = Pattern.compile("\\$\\{([^\\{\\}]+)\\}");

    /**
     * インスタンスを取得します.
     *
     * @return {@link BindUtils}のインスタンス
     */
    public static BindUtils getInstance() {
        return instance;
    }

    /**
     * プライベートコンストラクタ.
     */
    private BindUtils() {
        // Do Nothing...
    }

    /**
     * オブジェクトの全フィールドに対して各種変数およびプロファイルの値のバインド処理を行う.
     *
     * @param target 対象オブジェクト
     * @param globalVariables グローバル変数
     * @param scenarioVariables シナリオ変数
     */
    public void bind(Object target, Map<String, String> profiles, Map<String, Object> globalVariables,
            Map<String, Object> scenarioVariables, Map<String, Object> flowVariables) {

        // 対象のクラスを取得
        Class clazz = target.getClass();

        while (true) {

            // String以外
            for (Field f : clazz.getDeclaredFields()) {

                if (f.getName().equals("serialVersionUID")) {
                    continue;
                }

                Class<?> fieldClass = f.getType();

                try {
                    Object value = PropertyUtils.getProperty(target, f.getName());
                    if (value != null) {

                        if (String.class.isAssignableFrom(fieldClass)) {
                            value = bind(value.toString(), profiles, globalVariables, scenarioVariables, flowVariables);
                            BeanUtils.setProperty(target, f.getName(), value);
                            // pd.getWriteMethod().invoke(target, value.toString());

                        } else if (fieldClass.isArray()) {
                            Object[] array = (Object[]) value;
                            for (Object o : array) {
                                bind(o, profiles, globalVariables, scenarioVariables, flowVariables);
                            }
                        } else if (Map.class.isAssignableFrom(fieldClass)) {
                            Map<?, ?> checkMap = Map.class.cast(value);
                            Map.Entry<?, ?> checkEntry = checkMap.entrySet().iterator().next();
                            if (String.class.isAssignableFrom(checkEntry.getKey().getClass())) {
                                // KeyはStringで固定制約をツールとして設ける
                                Map<String, ?> map = Map.class.cast(value);
                                for (Map.Entry entry : map.entrySet()) {
                                    Object entryValue = entry.getValue();
                                    if (String.class.isAssignableFrom(entryValue.getClass())) {
                                        String entryValueString = String.class.cast(entryValue);
                                        String bindedEntryValueStrig = bind(entryValueString, profiles, globalVariables,
                                                scenarioVariables, flowVariables);
                                        Map<String, String> map2 = (Map<String, String>) map;
                                        map2.put(entry.getKey().toString(), bindedEntryValueStrig);
                                    } else {
                                        bind(entry.getValue(), profiles, globalVariables, scenarioVariables,
                                                flowVariables);
                                    }
                                }
                            } else {
                                throw new SystemException(CoreMessages.CORE_ERR_0013, clazz.getName());
                            }
                        } else if (List.class.isAssignableFrom(fieldClass)) {
                            List<?> list = List.class.cast(value);
                            list.getClass().getTypeParameters();
                            List<String> bindedStringList = new ArrayList<>();
                            for (Object element : list) {
                                if (String.class.isAssignableFrom(element.getClass())) {
                                    String elementString = String.class.cast(element);
                                    elementString = bind(elementString, profiles, globalVariables, scenarioVariables,
                                            flowVariables);
                                    bindedStringList.add(elementString);
                                } else {
                                    bind(element, profiles, globalVariables, scenarioVariables, flowVariables);
                                }
                            }
                            if (!bindedStringList.isEmpty()) {
                                list.clear();
                                List<String> stringList = (List<String>) list;
                                stringList.addAll(bindedStringList);
                            }
                        } else {
                            bind(value, profiles, globalVariables, scenarioVariables, flowVariables);
                        }
                    }

                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    log.debug("Ignore...", e);
                }

            }

            if (clazz.getSuperclass() != null) {
                // 親クラスが存在すれば、ループ処理.
                // 再帰でもよかったなぁ・・・
                clazz = clazz.getSuperclass();
            } else {
                // 存在しなければループを抜ける
                break;
            }
        }
    }

    /**
     * 文字列に対して各種変数およびプロファイルの値のバインド処理を行う.
     *
     * @param target 対象文字列
     * @param profiles プロファイル
     * @param globalVariables グローバル変数
     * @param scenarioVariables シナリオ変数
     * @param flowVariables Flow変数
     * @return バインド後の文字列
     */
    public String bind(String target, @NonNull Map<String, String> profiles,
            @NonNull Map<String, Object> globalVariables, @NonNull Map<String, Object> scenarioVariables,
            @NonNull Map<String, Object> flowVariables) {

        if (StringUtils.isEmpty(target)) {
            return null;
        }
        return replaceVariable(target, null, profiles, globalVariables, scenarioVariables, flowVariables);

    }

    /**
     * 変数での置換対象文字列を置換して返却する. 置換対象文字列から置換後文字列を解決できた場合には、本メソッドを再帰呼び出しして多重置換後の文字列を解決する.
     * 同一の置換対象文字列が置換対象履歴に含まれる場合は、循環参照と判断しエラーとする.
     * <p>
     * EX) ${hoge} -> ${fuga} -> ${hoge} で循環する例 対象文字列 : abc${hoge}123${fuga}
     * profiles : hoge=${fuga}, huga=${hoge}
     *
     * @param target 対象文字列
     * @param targetHistory 置換履歴
     * @param profiles プロファイル変数
     * @param globalVariables グローバル変数
     * @param scenarioVariables シナリオ変数
     * @param flowVariables Flow変数
     * @return 置換後対象文字列
     */
    private String replaceVariable(String target, List<String> targetHistory, @NonNull Map<String, String> profiles,
            @NonNull Map<String, Object> globalVariables, @NonNull Map<String, Object> scenarioVariables,
            @NonNull Map<String, Object> flowVariables) {

        if (StringUtils.isEmpty(target)) {
            return null;
        }

        var needCircularReferenceCheck = targetHistory != null;
        if (!needCircularReferenceCheck) {
            targetHistory = new ArrayList<>();
        }

        var m = BIND_EXTRACT_PATTERN.matcher(target);

        while (m.find()) {

            var referKey = m.group(1);

            if (needCircularReferenceCheck && targetHistory.contains(referKey)) {
                throw new SystemException(CoreMessages.CORE_ERR_0075, referKey);
            }
            targetHistory.add(referKey);

            // 置換後の文字列
            var replaceString = (String) null;

            if (referKey.matches("[^\\.]+\\..+")) {
                // 名前空間あり
                var referKeyArray = referKey.split("\\.");
                var referNameSpace = referKeyArray[0];
                var referKeyInNameSpace = referKeyArray[1];

                // 参照変数種別（スコープ）の解決
                var referenceVariableType = ReferenceVariableType.valueOfByName(referNameSpace);

                if (referenceVariableType == null) {
                    throw new SystemException(CoreMessages.CORE_ERR_0004, referKey);
                }

                switch (referenceVariableType) {
                case FIX:
                    replaceString = replaceVariable(referKeyInNameSpace, targetHistory, profiles, globalVariables,
                            scenarioVariables, flowVariables);
                    break;
                case GLOBAL:
                    // グローバルスコープ変数からのバインド
                    if (globalVariables.containsKey(referKeyInNameSpace)) {
                        replaceString = replaceVariable(globalVariables.get(referKeyInNameSpace).toString(),
                                targetHistory, profiles, globalVariables, scenarioVariables, flowVariables);
                    }
                    break;
                case SCENARIO:
                    // シナリオスコープ変数からのバインド
                    if (scenarioVariables.containsKey(referKeyInNameSpace)) {
                        replaceString = replaceVariable(scenarioVariables.get(referKeyInNameSpace).toString(),
                                targetHistory, profiles, globalVariables, scenarioVariables, flowVariables);
                    }
                    break;
                case FLOW:
                    // Flowスコープ変数からのバインド
                    if (flowVariables.containsKey(referKeyInNameSpace)) {
                        replaceString = replaceVariable(flowVariables.get(referKeyInNameSpace).toString(),
                                targetHistory, profiles, globalVariables, scenarioVariables, flowVariables);
                    }
                    break;
                default:
                    throw new SystemException(CoreMessages.CORE_ERR_0004, referKey);
                }
            } else {
                // 名前空間なし
                if (profiles.containsKey(referKey)) {
                    replaceString = replaceVariable(profiles.get(referKey), targetHistory, profiles, globalVariables,
                            scenarioVariables, flowVariables);
                } else {
                    log.warn(MessageManager.getInstance().getMessage(CoreMessages.CORE_WRN_0004, m.group(), target));
                }
            }

            if (StringUtils.isNotEmpty(replaceString)) {
                log.debug("replace target:{}, bind:{}", m.group(0), replaceString);
                target = target.replace(m.group(0), replaceString);
                // 置換を行った場合は再度Matcherを作成し直す、置換できなかった文字列も再度やり直すことになるが仕方ない。
                // 全ての置換可能な文字列を置換し終えた場合はこのルートに入らないので、Whileが回りきって返却される。
                m = BIND_EXTRACT_PATTERN.matcher(target);
            }
        }
        return target;
    }

}
