package com.epion_t3.core.context;

import com.epion_t3.core.type.ScenarioManageFileSystem;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * オプション.
 */
@Getter
@Setter
public class Option implements Serializable {

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
    private Boolean noreport = false;

}
