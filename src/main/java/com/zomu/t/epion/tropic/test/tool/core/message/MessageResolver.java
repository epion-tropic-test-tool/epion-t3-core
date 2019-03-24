package com.zomu.t.epion.tropic.test.tool.core.message;

import com.google.common.reflect.ClassPath;
import com.zomu.t.epion.tropic.test.tool.core.exception.MessageNotFoundException;
import com.zomu.t.epion.tropic.test.tool.core.exception.SystemException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.util.*;
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
     * メッセージのリソースファイルを
     */
    public static final String MESSAGE_SUFFIX_PATTERN = "_messages.properties";

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
                .getObject(messageCode).toString();
    }

    /**
     * メッセージのロード.
     */
    private void load() {

        log.debug("start message resolver initial load.");

        List<ResourceBundle> loadingResourceBundles = new ArrayList<>();

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        List<ClassPath.ResourceInfo> messageResources
                = null;
        try {
            messageResources = ClassPath.from(loader)
                    .getResources().stream()
                    .filter(info -> info.getResourceName().endsWith(MESSAGE_SUFFIX_PATTERN))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new SystemException(e);
        }

        for (ClassPath.ResourceInfo resource : messageResources) {
            try {
                ResourceBundle rb =
                        ResourceBundle.getBundle(FilenameUtils.getBaseName(resource.getResourceName()), Locale.getDefault());
                loadingResourceBundles.add(rb);
                log.debug("load resource bundle: {}", resource.getResourceName());
            } catch (MissingResourceException e) {
                log.debug("can not load resource bundle: {}", resource.getResourceName());
                continue;
            }
        }

        // 読み取り専用とする
        resourceBundles = Collections.unmodifiableList(loadingResourceBundles);

        log.debug("end message resolver initial load.");
    }

}
