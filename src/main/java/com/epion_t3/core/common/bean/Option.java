/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.common.bean;

import com.epion_t3.core.common.type.PathResolveMode;
import com.epion_t3.core.common.type.ScenarioManageFileSystem;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.nio.file.Path;

/**
 * オプション.
 */
@Getter
@Setter
public class Option implements Serializable {

    /**
     * デフォルトシリアルバージョンUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * バージョン.
     */
    private String version;

    /**
     * 対象.
     */
    private String target;

    /**
     * シナリオ配置ルートパス.
     */
    private String rootPath;

    /**
     * 結果配置ルートパス.
     */
    private String resultRootPath;

    /**
     * シナリオ管理ファイルシステム.
     */
    private ScenarioManageFileSystem filesystem = ScenarioManageFileSystem.LOCAL_FILESYSTEM;

    /**
     * 実行プロファイル.
     */
    private String profile;

    /**
     * 実行モード.
     */
    private String mode;

    /**
     * デバッグモード指定フラグ.
     */
    private Boolean debug = false;

    /**
     * レポートを出力拒否フラグ.
     */
    private Boolean noReport = false;

    /**
     * コンソールレポート出力フラグ. true : 出力
     */
    private Boolean consoleReport = false;

    /**
     * レポートのWEBアセット参照パス. cssやjsを配置するベースパス.
     */
    private String webAssetPath;

    /**
     * パス解決モード.
     * 
     * @since 0.0.5
     */
    private PathResolveMode pathResolveMode;

}
