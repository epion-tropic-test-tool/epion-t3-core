/* Copyright (c) 2017-2020 Nozomu Takashima. */
package com.epion_t3.core.common.bean;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Flow設計情報.
 *
 * @author takashno
 */
@Getter
@Setter
public class FlowSpecInfo implements Serializable {

    /**
     * デフォルトシリアルバージョンUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * コマンドID.
     */
    private String id;

    /**
     * 機能説明.
     */
    private Map<Locale, List<String>> functions = new ConcurrentHashMap<>();

    /**
     * 試験項目書文言.
     */
    private Map<Locale, List<String>> testItems = new ConcurrentHashMap<>();

    /**
     * コマンド構成. 指定しなければいけない要素等を記載する.
     */
    private Map<String, FlowSpecStructure> structures = new ConcurrentHashMap<>();

    /**
     * 機能を追加.
     *
     * @param lang ロケール名
     * @param contents コンテンツ
     */
    public void addFunction(String lang, String contents) {
        Locale locale = Locale.forLanguageTag(lang);
        if (!functions.containsKey(locale)) {
            functions.put(locale, new ArrayList<>());
        }
        functions.get(locale).add(contents);
    }

    /**
     * 試験項目を追加.
     *
     * @param lang ロケール名
     * @param contents コンテンツ
     */
    public void addTestItem(String lang, String contents) {
        Locale locale = Locale.forLanguageTag(lang);
        if (!testItems.containsKey(locale)) {
            testItems.put(locale, new ArrayList<>());
        }
        testItems.get(locale).add(contents);
    }

    /**
     * Flow構成を追加.
     *
     * @param flowSpecStructure Flow構成情報
     */
    public void addStructure(FlowSpecStructure flowSpecStructure) {
        structures.put(flowSpecStructure.getName(), flowSpecStructure);
    }

    /**
     * Flowを構成する要素を取得.
     *
     * @param name
     * @return
     */
    @Nullable
    public FlowSpecStructure getStructure(String name) {
        return structures.get(name);
    }
}
