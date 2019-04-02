package com.epion_t3.core.context;

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
