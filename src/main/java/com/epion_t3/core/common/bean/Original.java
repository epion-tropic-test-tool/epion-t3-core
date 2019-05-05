package com.epion_t3.core.common.bean;

import com.epion_t3.core.common.bean.scenario.Configuration;
import com.epion_t3.core.common.bean.scenario.Command;
import com.epion_t3.core.common.bean.scenario.ET3Base;
import lombok.Getter;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * YAMLから読み込んだ情報の原本を保存するためのもの.
 *
 * @author takashno
 */
@Getter
public class Original implements Serializable {

    /**
     * デフォルトシリアルバージョンUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * ファイルそのままの原本.
     * キー：infoのid値
     * 値：ファイルの解析結果そのまま
     */
    private final Map<String, ET3Base> originals = new ConcurrentHashMap<>();

    /**
     * 設定の原本.
     * キー：infoのid + '-' + 設定の要素のid値 = 設定識別子
     */
    private final Map<String, Configuration> configurations = new ConcurrentHashMap<>();

    /**
     * コマンドの原本.
     * キー：infoのid + '-' + コマンドの要素のid値　= コマンド識別子
     */
    private final Map<String, Command> commands = new ConcurrentHashMap<>();

    /**
     * コマンドの識別子とシナリオの関係マップ.
     * キー：infoのid + '@' + コマンドの要素のid値　= コマンド識別子
     * 値：infoのid
     */
    private final Map<String, String> commandScenarioRelations = new ConcurrentHashMap<>();

    /**
     * 設定の識別子とシナリオの関係マップ
     * キー：infoのid + '@' + 設定の要素のid値　= 設定識別子
     * 値：infoのid
     */
    private final Map<String, String> configurationScenarioRelations = new ConcurrentHashMap<>();

    /**
     * scenario配置ディレクトリマップ.
     * キー：infoのid
     * 値：Path
     */
    private final Map<String, Path> scenarioPlacePaths = new ConcurrentHashMap<>();

    /**
     * コマンド配置ディレクトリマップ.
     * キー：infoのid + '@' + コマンドの要素のid値　= コマンド識別子
     * 値：Path
     */
    private final Map<String, Path> commandPlacePaths = new ConcurrentHashMap<>();

    /**
     * 設定配置ディレクトリマップ.
     * キー：infoのid + '@' + 設定の要素のid値　= 設定識別子
     * 値：Path
     */
    private final Map<String, Path> configurationPlacePaths = new ConcurrentHashMap<>();

    /**
     * プロファイル.
     */
    private final Map<String, Map<String, String>> profiles = new ConcurrentHashMap<>();


}
