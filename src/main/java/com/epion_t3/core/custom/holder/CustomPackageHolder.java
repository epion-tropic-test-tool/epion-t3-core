package com.epion_t3.core.custom.holder;

import com.epion_t3.core.common.bean.*;
import com.epion_t3.core.common.bean.scenario.Command;
import com.epion_t3.core.common.bean.scenario.Configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * カスタムパッケージの保持クラス.
 *
 * @author takashno
 */
public final class CustomPackageHolder {

    /**
     * インスタンス.
     */
    private static final CustomPackageHolder instance = new CustomPackageHolder();

    /**
     * カスタムパッケージ.
     * Key：カスタム名
     * Value：カスタムパッケージ
     */
    private final Map<String, String> customPackages = new ConcurrentHashMap<>();

    /**
     * カスタムコマンド.
     * Key:
     * Value:
     */
    private final Map<String, CommandInfo> customCommands = new ConcurrentHashMap<>();

    /**
     * カスタムFlow.
     * Key:
     * Value:
     */
    private final Map<String, FlowInfo> customFlows = new ConcurrentHashMap<>();

    /**
     * カスタム設定.
     * Key: id
     * Value: カスタム設定
     */
    private static final Map<String, Configuration> customConfigurations = new ConcurrentHashMap<>();

    /**
     * カスタム設定.
     * Key: configurationId
     * Value: カスタム設定リスト
     */
    private static final Map<String, List<Configuration>> customConfigurationTypes = new ConcurrentHashMap<>();

    /**
     * カスタム設定情報.
     */
    private static final Map<String, CustomConfigurationInfo> customConfigurationInfos = new ConcurrentHashMap<>();

    /**
     * カスタム設計情報.
     * Key: カスタム名
     * Value: カスタム設計情報
     */
    private final Map<String, CustomSpecInfo> customSpecs = new ConcurrentHashMap<>();

//    /**
//     * カスタムコマンド設計情報.
//     * Key: カスタムコマンドID
//     * Value: カスタムコマンド設計情報
//     */
//    private final Map<String, CommandSpecInfo> customCommandSpecs = new ConcurrentHashMap<>();

    /**
     * カスタムコマンド設計情報.
     * Key: カスタムコマンドモデルクラス
     * Value: カスタムコマンド設計情報
     */
    private final Map<Class<? extends Command>, CommandSpecInfo> customCommandSpecs = new ConcurrentHashMap<>();


    /**
     * プライベートコンストラクタ.
     */
    private CustomPackageHolder() {
        // Do Nothing...
    }

    /**
     * インスタンスを取得する.
     *
     * @return シングルトンインスタンス
     */
    public static CustomPackageHolder getInstance() {
        return instance;
    }

    // -----------------------------------------------------------------------------------------------------------

    /**
     * カスタムパッケージを追加する.
     *
     * @param customName
     * @param packageaName
     */
    public void addCustomPackage(String customName, String packageaName) {
        customPackages.put(customName, packageaName);
    }

    /**
     * カスタムパッケージを取得する.
     *
     * @param customName
     * @return
     */
    public String getCustomPackage(String customName) {
        return customPackages.get(customName);
    }

    /**
     * カスタムパッケージマップを取得する.
     *
     * @return 変更不可Map
     */
    public Map<String, String> getCustomPackages() {
        return Collections.unmodifiableMap(customPackages);
    }

    // -----------------------------------------------------------------------------------------------------------

    public void addCustomCommandInfo(String commandId, CommandInfo commandInfo) {
        customCommands.put(commandId, commandInfo);
    }

    public CommandInfo getCustomCommandInfo(String commandId) {
        return customCommands.get(commandId);
    }

    // -----------------------------------------------------------------------------------------------------------

    /**
     * カスタムFlowを追加する.
     *
     * @param flowId
     * @param flowInfo
     */
    public void addCustomFlowInfo(String flowId, FlowInfo flowInfo) {
        customFlows.put(flowId, flowInfo);
    }

    /**
     * カスタムFlowを取得する.
     *
     * @param flowId
     * @return
     */
    public FlowInfo getCustomFlowInfo(String flowId) {
        return customFlows.get(flowId);
    }

    // -----------------------------------------------------------------------------------------------------------

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

    // -----------------------------------------------------------------------------------------------------------

    public void addCustomSpec(String customName, CustomSpecInfo customSpecInfo) {
        customSpecs.put(customName, customSpecInfo);
    }

    public CustomSpecInfo getCustomSpec(String customName) {
        return customSpecs.get(customName);
    }

    public CommandSpecInfo getCommandSpec(String customName, String commandId) {
        return getCustomSpec(customName).getCommands().get(commandId);
    }

    // -----------------------------------------------------------------------------------------------------------

    public void addCustomCommandSpec(Class<? extends Command>  commandModelClass, CommandSpecInfo commandSpecInfo) {
        customCommandSpecs.put(commandModelClass, commandSpecInfo);
    }

    public CommandSpecInfo getCustomCommandSpec(Class<? extends Command> commandModelClass) {
        return customCommandSpecs.get(commandModelClass);
    }

    // -----------------------------------------------------------------------------------------------------------


}
