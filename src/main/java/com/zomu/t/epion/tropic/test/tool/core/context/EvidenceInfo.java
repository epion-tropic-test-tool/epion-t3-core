package com.zomu.t.epion.tropic.test.tool.core.context;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.UUID;

/**
 * エビデンス情報.
 *
 * @author takashno
 */
@Getter
@Setter
public class EvidenceInfo implements Serializable {

    /**
     * エビデンスID.
     * 内部的に割り振っているのみ
     */
    private UUID evidenceId = UUID.randomUUID();

    /**
     * 完全シナリオ名称.
     * Full Query Scenario Name.
     */
    private String fqsn;

    /**
     * 完全Flow名称.
     * Full Query Flow Name.
     */
    private String fqfn;

    /**
     * Flowの実行ID.
     * レポートのアンカーリンクのために必要.
     */
    private String executeFlowId;

    /**
     * エビデンス名.
     * ユーザーがつけるエビデンスへの論理名.
     */
    private String name;

}
