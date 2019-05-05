package com.epion_t3.core.common.util;

import com.epion_t3.core.exception.SystemException;
import com.epion_t3.core.message.MessageManager;
import com.epion_t3.core.message.impl.CoreMessages;
import com.epion_t3.core.common.type.ReferenceVariableType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.regex.Matcher;
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
     * バインド変数抽出パターン(名前空間あり).
     * TODO:ここは外側から指定できるべきか
     */
    public static final Pattern BIND_EXTRACT_PATTERN = Pattern.compile("\\$\\{([^\\}]+)\\}");

    /**
     * バインド変数抽出パターン(名前空間あり).
     * TODO:ここは外側から指定できるべきか
     */
    public static final Pattern BIND_EXTRACT_PATTERN_WITHNAMESPACE = Pattern.compile("\\$\\{([^\\.]+)\\.([^\\}]+)\\}");

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
     * @param target            対象オブジェクト
     * @param globalVariables   グローバル変数
     * @param scenarioVariables シナリオ変数
     */
    public void bind(Object target, Map<String, String> profiles,
                     Map<String, Object> globalVariables,
                     Map<String, Object> scenarioVariables,
                     Map<String, Object> flowVariables) {

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

                    PropertyDescriptor pd = new PropertyDescriptor(f.getName(), target.getClass());
                    Object value = pd.getReadMethod().invoke(target);
                    if (value != null) {

                        if (String.class.isAssignableFrom(fieldClass)) {
                            value = bind(value.toString(),
                                    profiles,
                                    globalVariables,
                                    scenarioVariables,
                                    flowVariables);
                            pd.getWriteMethod().invoke(target, value.toString());

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
                                        String bindedEntryValueStrig =
                                                bind(entryValueString, profiles, globalVariables, scenarioVariables, flowVariables);
                                        Map<String, String> map2 = (Map<String, String>) map;
                                        map2.put(entry.getKey().toString(), bindedEntryValueStrig);
                                    } else {
                                        bind(entry.getValue(), profiles, globalVariables, scenarioVariables, flowVariables);
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
                                    elementString = bind(elementString, profiles, globalVariables, scenarioVariables, flowVariables);
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

                } catch (IntrospectionException | IllegalAccessException | InvocationTargetException e) {
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
     * @param target            対象文字列
     * @param globalVariables   グローバル変数
     * @param scenarioVariables シナリオ変数
     * @return バインド後の文字列
     */
    public String bind(String target,
                       Map<String, String> profiles,
                       Map<String, Object> globalVariables,
                       Map<String, Object> scenarioVariables,
                       Map<String, Object> flowVariables) {

        if (StringUtils.isEmpty(target)) {
            return null;
        }

        // 名前空間なしでバインド（Profile用を想定）
        Matcher m = BIND_EXTRACT_PATTERN.matcher(target);

        // バインドを実行したかどうかのフラグ
        boolean replaceFlg = false;

        // 連続バインド失敗回数のカウンタ
        int loopCount = 0;

        while (m.find()) {

            String referProfileKey = m.group(1);

            if (profiles.containsKey(referProfileKey)) {
                log.trace("replace profile target:{}, bind:{}",
                        m.group(0), profiles.get(referProfileKey));
                target = target.replace(
                        m.group(0),
                        profiles.get(referProfileKey));
            }

            // バインドが成功していない継続数をインクリメントする
            if (!replaceFlg) {
                loopCount++;
            } else {
                // 一度でもバインドが成功した場合は、初期化する
                loopCount = 0;
            }

            if (loopCount > 10) {
                // 最大失敗回数は10回とする
                log.warn(MessageManager.getInstance().getMessage(CoreMessages.CORE_WRN_0001, target));
                break;
            }

            // 変数バインド継続
            m = BIND_EXTRACT_PATTERN.matcher(target);

        }

        // 名前空間ありにて抽出
        m = BIND_EXTRACT_PATTERN_WITHNAMESPACE.matcher(target);

        // 初期化
        loopCount = 0;

        while (m.find()) {
            ReferenceVariableType referenceVariableType = ReferenceVariableType.valueOfByName(m.group(1));
            if (referenceVariableType != null) {
                switch (referenceVariableType) {
                    case FIX:
                        log.trace("replace fix target:{}, bind:{}",
                                m.group(0), m.group(2));
                        target = target.replace(
                                m.group(0),
                                m.group(2));
                        replaceFlg = true;
                        break;
                    case GLOBAL:
                        // グローバルスコープ変数からのバインド
                        if (globalVariables.containsKey(m.group(2))) {
                            log.trace("replace global target:{}, bind:{}",
                                    m.group(0), globalVariables.get(m.group(2)).toString());
                            target = target.replace(
                                    m.group(0),
                                    globalVariables.get(m.group(2)).toString());
                            replaceFlg = true;
                        } else {
                            replaceFlg = false;
                        }
                        break;
                    case SCENARIO:
                        // シナリオスコープ変数からのバインド
                        if (scenarioVariables.containsKey(m.group(2))) {
                            log.trace("replace scenario target:{}, bind:{}",
                                    m.group(0), scenarioVariables.get(m.group(2)).toString());
                            target = target.replace(
                                    m.group(0),
                                    scenarioVariables.get(m.group(2)).toString());
                            replaceFlg = true;
                        } else {
                            replaceFlg = false;
                        }
                        break;
                    case FLOW:
                        if (flowVariables == null) {
                            // Flowスコープ変数からのバインドが利用不可だった場合、その時点で警告扱い
                            loopCount += 10;
                        } else {
                            // Flowスコープ変数からのバインド
                            if (flowVariables.containsKey(m.group(2))) {
                                log.trace("replace scenario target:{}, bind:{}",
                                        m.group(0), flowVariables.get(m.group(2)).toString());
                                target = target.replace(
                                        m.group(0),
                                        flowVariables.get(m.group(2)).toString());
                                replaceFlg = true;
                            } else {
                                replaceFlg = false;
                            }
                        }
                        break;
                    default:
                        throw new SystemException(CoreMessages.CORE_ERR_0004, m.group(0));
                }
            }

            // バインドが成功していない継続数をインクリメントする
            if (!replaceFlg) {
                loopCount++;
            } else {
                // 一度でもバインドが成功した場合は、初期化する
                loopCount = 0;
            }

            if (loopCount > 10) {
                // 最大失敗回数は10回とする
                log.warn(MessageManager.getInstance().getMessage(CoreMessages.CORE_WRN_0001, target));
                break;
            }

            // 変数バインド継続
            m = BIND_EXTRACT_PATTERN_WITHNAMESPACE.matcher(target);
        }

        return target;

    }


}
