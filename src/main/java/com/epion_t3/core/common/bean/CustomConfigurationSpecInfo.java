/* Copyright (c) 2017-2019 Nozomu Takashima. */
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

@Getter
@Setter
public class CustomConfigurationSpecInfo implements Serializable {

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
     * 設定構成. 指定しなければいけない要素等を記載する.
     */
    private Map<String, CustomConfigurationSpecStructure> structures = new ConcurrentHashMap<>();

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
     * 設定情報構成を追加.
     *
     * @param customConfigurationSpecStructure
     */
    public void addStructure(CustomConfigurationSpecStructure customConfigurationSpecStructure) {
        structures.put(customConfigurationSpecStructure.getName(), customConfigurationSpecStructure);
    }

    /**
     * 設定情報を構成する要素を取得.
     *
     * @param name
     * @return
     */
    @Nullable
    public CustomConfigurationSpecStructure getStructure(String name) {
        return structures.get(name);
    }
}
