package com.epion_t3.core.common.bean;

import com.epion_t3.core.common.bean.spec.Command;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author takashno
 */
@Getter
@Setter
public class CommandSpecStructure implements Serializable {

    /**
     * デフォルトシリアルバージョンUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * 名称.
     */
    private String name;

    /**
     * 型.
     */
    private String type;

    /**
     * 必須.
     */
    private Boolean required;

    /**
     * 正規表現パターン.
     */
    private String pattern;

    /**
     * 概要説明.
     */
    private Map<Locale, String> summaries = new ConcurrentHashMap<>();

    /**
     * 詳細説明.
     */
    private Map<Locale, String> descriptions = new ConcurrentHashMap<>();

    /**
     * プロパティ.
     * typeがobjectの場合に、ネスト構造となるため子階層を表す.
     */
    private List<CommandSpecStructure> property = new ArrayList<>();

    /**
     * 概要を追加.
     *
     * @param lang     ロケール名
     * @param contents コンテンツ
     */
    public void putSummary(String lang, String contents) {
        summaries.put(Locale.forLanguageTag(lang), contents);
    }

    /**
     * 詳細を追加.
     *
     * @param lang     ロケール名
     * @param contents コンテンツ
     */
    public void putDescription(String lang, String contents) {
        descriptions.put(Locale.forLanguageTag(lang), contents);
    }

}
