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
     * バインド変数抽出パターン(名前空間あり). TODO:ここは外側から指定できるべきか
     */
    public static final Pattern BIND_EXTRACT_PATTERN = Pattern.compile("\\$\\{([^\\.\\{\\}]+)\\}");

    /**
     * バインド変数抽出パターン(名前空間あり). TODO:ここは外側から指定できるべきか
     */
    public static final Pattern BIND_EXTRACT_PATTERN_WITHNAMESPACE = Pattern
            .compile("\\$\\{([^\\.\\{\\}]+)\\.([^\\{\\}]+)\\}");

    /**
     * インスタンスを取得します.
     *
     * @return
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

//            Arrays.stream(clazz.getDeclaredFields())
//                    // Stringのみ
//                    .filter(x -> String.class.isAssignableFrom(x.getType()))
//                    .forEach(x -> {
//                        try {
//                            PropertyDescriptor pd = new PropertyDescriptor(x.getName(), target.getClass());
//                            Object value = pd.getReadMethod().invoke(target);
//                            if (value != null) {
//                                value = bind(value.toString(),
//                                        profiles,
//                                        globalVariables,
//                                        scenarioVariables,
//                                        flowVariables);
//                                pd.getWriteMethod().invoke(target, value.toString());
//                            }
//                        } catch (IntrospectionException | IllegalAccessException | InvocationTargetException e) {
//                            log.debug("Ignore...", e);
//                        }
//                    });

            // String以外
            for (Field f : clazz.getDeclaredFields()) {

                if (f.getName().equals("serialVersionUID")) {
                    continue;
                }

                Class<?> fieldClass = f.getType();

                try {
                    // PropertyDescriptor pd = new PropertyDescriptor(f.getName(),
                    // target.getClass());
                    // Object value = pd.getReadMethod().invoke(target);
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

//            Arrays.stream(clazz.getDeclaredFields())
//                    // String以外
//                    .filter(x -> !String.class.isAssignableFrom(x.getType()) && !x.getName().equals("serialVersionUID"))
//                    .forEach(x -> {
//                        try {
//                            PropertyDescriptor pd = new PropertyDescriptor(x.getName(), target.getClass());
//                            Object value = pd.getReadMethod().invoke(target);
//                            if (value != null) {
//                                bind(value,
//                                        profiles,
//                                        globalVariables,
//                                        scenarioVariables,
//                                        flowVariables);
//                            }
//                        } catch (IntrospectionException | IllegalAccessException | InvocationTargetException e) {
//                            log.debug("Ignore...", e);
//                        }
//                    });

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

        // 名前空間なしでバインド（Profile用を想定）
        var m = BIND_EXTRACT_PATTERN.matcher(target);

        // プロファイルからのバインド
        while (m.find()) {

            var referProfileKey = m.group(1);

            if (profiles.containsKey(referProfileKey)) {
                log.debug("replace profile target:{}, bind:{}", m.group(0), profiles.get(referProfileKey));
                target = target.replace(m.group(0), profiles.get(referProfileKey));
                // バインドしたあとの文字列に更にバインド文字列が含まれている場合、 多重でバインドを行うため、Matcherを再度生成する
                m = BIND_EXTRACT_PATTERN.matcher(target);
            } else {
                log.warn(MessageManager.getInstance().getMessage(CoreMessages.CORE_WRN_0004, m.group(), target));
            }

        }

        // 名前空間ありにて抽出
        m = BIND_EXTRACT_PATTERN_WITHNAMESPACE.matcher(target);

        while (m.find()) {

            // バインドを実行したかどうかのフラグ
            var replaceSuccess = false;

            // 参照変数種別（スコープ）の解決
            var referenceVariableType = ReferenceVariableType.valueOfByName(m.group(1));

            if (referenceVariableType == null) {
                throw new SystemException(CoreMessages.CORE_ERR_0004, m.group(0));
            } else {
                switch (referenceVariableType) {
                case FIX:
                    log.debug("replace fix target:{}, bind:{}", m.group(0), m.group(2));
                    target = target.replace(m.group(0), m.group(2));
                    replaceSuccess = true;
                    break;
                case GLOBAL:
                    // グローバルスコープ変数からのバインド
                    if (globalVariables.containsKey(m.group(2))) {
                        log.debug("replace global target:{}, bind:{}", m.group(0),
                                globalVariables.get(m.group(2)).toString());
                        target = target.replace(m.group(0), globalVariables.get(m.group(2)).toString());
                        replaceSuccess = true;
                    }
                    break;
                case SCENARIO:
                    // シナリオスコープ変数からのバインド
                    if (scenarioVariables.containsKey(m.group(2))) {
                        log.debug("replace scenario target:{}, bind:{}", m.group(0),
                                scenarioVariables.get(m.group(2)).toString());
                        target = target.replace(m.group(0), scenarioVariables.get(m.group(2)).toString());
                        replaceSuccess = true;
                    }
                    break;
                case FLOW:
                    // Flowスコープ変数からのバインド
                    if (flowVariables.containsKey(m.group(2))) {
                        log.debug("replace scenario target:{}, bind:{}", m.group(0),
                                flowVariables.get(m.group(2)).toString());
                        target = target.replace(m.group(0), flowVariables.get(m.group(2)).toString());
                        replaceSuccess = true;
                    }
                    break;
                default:
                    throw new SystemException(CoreMessages.CORE_ERR_0004, m.group(0));
                }
            }

            if (replaceSuccess) {
                // バインドしたあとの文字列に更にバインド文字列が含まれている場合、 多重でバインドを行うため、Matcherを再度生成する
                m = BIND_EXTRACT_PATTERN_WITHNAMESPACE.matcher(target);
            } else {
                // バインドに失敗した場合は、WARNログを出して次に進む
                log.warn(MessageManager.getInstance().getMessage(CoreMessages.CORE_WRN_0001, m.group(0), target));
            }

        }

        return target;

    }

}
