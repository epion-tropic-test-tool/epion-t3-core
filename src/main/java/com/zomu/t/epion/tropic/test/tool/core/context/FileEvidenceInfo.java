package com.zomu.t.epion.tropic.test.tool.core.context;

import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;

@Getter
@Setter
public class FileEvidenceInfo extends EvidenceInfo {

    /**
     * パス.
     */
    private Path path;

    /**
     * 【レポート用】
     * シナリオディレクトリからの相対パス.
     */
    private String relativePath;

}
