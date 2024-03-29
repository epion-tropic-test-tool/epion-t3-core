/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.message;

import com.epion_t3.core.exception.MessageNotFoundException;
import com.epion_t3.core.exception.SystemException;
import com.google.common.reflect.ClassPath;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * メッセージ解決クラス.
 *
 * @author takashno
 */
@Slf4j
public final class MessageResolver {

    /**
     * メッセージ解決処理.
     */
    private static final MessageResolver instance = new MessageResolver();

    /**
     * リソースバンドルの保持リスト.
     */
    private static List<ResourceBundle> resourceBundles;

    /**
     * メッセージのリソースファイル判断用の接尾辞.
     */
    public static final String MESSAGE_SUFFIX_PATTERN = "_messages.properties";

    /**
     * メッセージのリソースファイル判断用の正規表現.
     */
    public static final String MESSAGE_REGEXP_PATTERN = "([^_]+)_messages_([^\\.]+)\\.properties";

    /**
     * プライベートコンストラクタ.
     */
    private MessageResolver() {

        // 読み込み
        load();
    }

    /**
     * インスタンスを取得する.
     *
     * @return シングルトンインスタンス
     */
    static MessageResolver getInstance() {
        return instance;
    }

    /**
     * メッセージを取得する.
     *
     * @param messageCode メッセージコード
     * @return メッセージ文字列
     */
    public String getMessage(String messageCode) {
        return resourceBundles.stream()
                .filter(x -> x.containsKey(messageCode))
                .findFirst()
                .orElseThrow(() -> new MessageNotFoundException(messageCode))
                .getObject(messageCode)
                .toString();
    }

    /**
     * メッセージのロード.
     */
    private void load() {

        log.debug("start message resolver initial load.");

        List<ResourceBundle> loadingResourceBundles = new ArrayList<>();

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        var messageResources = (List<ClassPath.ResourceInfo>) null;
        try {
            messageResources = ClassPath.from(loader)
                    .getResources()
                    .stream()
                    .filter(info -> info.getResourceName().endsWith(MESSAGE_SUFFIX_PATTERN)
                            || info.getResourceName().matches(MESSAGE_REGEXP_PATTERN))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new SystemException(e);
        }

        for (var resource : messageResources) {
            try {
                ResourceBundle rb = ResourceBundle.getBundle(FilenameUtils.getBaseName(resource.getResourceName()),
                        Locale.getDefault());
                loadingResourceBundles.add(rb);
                log.debug("load resource bundle: {}", resource.getResourceName());
            } catch (MissingResourceException e) {
                log.debug("can not load resource bundle: {}", resource.getResourceName());
            }
        }

        // 読み取り専用とする
        resourceBundles = Collections.unmodifiableList(loadingResourceBundles);

        log.debug("end message resolver initial load.");
    }

}
