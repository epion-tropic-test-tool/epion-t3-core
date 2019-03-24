package com.zomu.t.epion.tropic.test.tool.core.context;

import lombok.Getter;
import lombok.Setter;

/**
 * オブジェクトエビデンス.
 */
@Getter
@Setter
public class ObjectEvidenceInfo extends EvidenceInfo {

    /**
     * エビデンスオブジェクト.
     */
    private Object object;
}
