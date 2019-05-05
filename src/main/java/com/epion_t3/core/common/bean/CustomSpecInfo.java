package com.epion_t3.core.common.bean;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * カスタマイズ機能の情報
 *
 * @author takashno
 */
@Getter
@Setter
public class CustomSpecInfo implements Serializable {

    /**
     * デフォルトシリアルバージョンUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * カスタム機能名.
     */
    private String name;

    /**
     * カスタムパッケージ.
     */
    private String customPackage;

    /**
     * 概要説明.
     */
    private final Map<Locale, String> summaries = new ConcurrentHashMap<>();

    /**
     * 詳細説明.
     */
    private final Map<Locale, String> descriptions = new ConcurrentHashMap<>();

    /**
     * コマンド情報
     */
    private final Map<String, CommandSpecInfo> commands = new ConcurrentHashMap<>();

    /**
     * メッセージ.
     */
    private Map<Locale, List<Message>> messages = new ConcurrentHashMap<>();

    /**
     * 概要を追加.
     *
     * @param lang     ロケール名
     * @param contents コンテンツ
     */
    public void putSummary(String lang, String contents) {
        if (lang != null && StringUtils.isNotEmpty(contents)) {
            summaries.put(Locale.forLanguageTag(lang), contents);
        }
    }

    /**
     * 詳細を追加.
     *
     * @param lang     ロケール名
     * @param contents コンテンツ
     */
    public void putDescription(String lang, String contents) {
        if (lang != null && StringUtils.isNotEmpty(contents)) {
            descriptions.put(Locale.forLanguageTag(lang), contents);
        }
    }

    /**
     * コマンド設計情報へ追加.
     *
     * @param commandSpecInfo
     */
    public void putCommandSpec(CommandSpecInfo commandSpecInfo) {
        commands.put(commandSpecInfo.getId(), commandSpecInfo);
    }

    /**
     * メッセージを追加.
     *
     * @param lang
     * @param id
     * @param content
     */
    public void addMessage(String lang, String id, String content) {
        Locale locale = Locale.forLanguageTag(lang);
        if (!messages.containsKey(locale)) {
            messages.put(locale, new ArrayList<>());
        }
        Message message = new Message();
        message.setId(id);
        message.setContent(content);
        messages.get(locale).add(message);
    }

}
