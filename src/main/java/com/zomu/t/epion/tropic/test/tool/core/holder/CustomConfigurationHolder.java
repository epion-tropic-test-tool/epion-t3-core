package com.zomu.t.epion.tropic.test.tool.core.holder;

import com.zomu.t.epion.tropic.test.tool.core.context.CustomConfigurationInfo;
import com.zomu.t.epion.tropic.test.tool.core.model.scenario.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * カスタム設定の保持クラス.
 *
 * @author takashno
 */
public final class CustomConfigurationHolder {

    /**
     * シングルトンインスタンス.
     */
    private static final CustomConfigurationHolder instance = new CustomConfigurationHolder();

    /**
     * カスタム設定.
     * キー：id, 値：カスタム設定
     */
    private static final Map<String, Configuration> customConfigurations = new ConcurrentHashMap<>();

    /**
     * カスタム設定.
     * キー：configurationId, 値：カスタム設定リスト
     */
    private static final Map<String, List<Configuration>> customConfigurationTypes = new ConcurrentHashMap<>();

    /**
     * カスタム背定情報.
     */
    private static final Map<String, CustomConfigurationInfo> customConfigurationInfos = new ConcurrentHashMap<>();


    /**
     * プライベートコンストラクタ.
     */
    private CustomConfigurationHolder() {
        // Do Nothing...
    }

    /**
     * インスタンス取得.
     *
     * @return シングルトンインスタンス
     */
    public static CustomConfigurationHolder getInstance() {
        return instance;
    }

    /**
     * カスタム設定追加.
     * ２つのマップに対して追加処理を行う.
     *
     * @param customConfiguration カスタム設定
     */
    public void addCustomConfiguration(Configuration customConfiguration) {
        customConfigurations.put(customConfiguration.getId(), customConfiguration);
        if (!customConfigurationTypes.containsKey(customConfiguration.getConfiguration())) {
            customConfigurationTypes.put(customConfiguration.getConfiguration(), new ArrayList<>());
        }
        customConfigurationTypes.get(customConfiguration.getConfiguration()).add(customConfiguration);
    }

    /**
     * カスタム設定情報追加.
     *
     * @param customConfigurationInfo カスタム設定情報
     */
    public void addCustomConfigurationInfo(CustomConfigurationInfo customConfigurationInfo) {
        customConfigurationInfos.put(customConfigurationInfo.getId(), customConfigurationInfo);
    }

    /**
     * カスタム設定情報取得.
     *
     * @param id カスタム設定ID
     * @return カスタム設定情報
     */
    public CustomConfigurationInfo getCustomConfigurationInfo(String id) {
        return customConfigurationInfos.get(id);
    }
}
