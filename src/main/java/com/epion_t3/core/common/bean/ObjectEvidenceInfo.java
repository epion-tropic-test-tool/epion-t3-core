package com.epion_t3.core.common.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * オブジェクトエビデンス.
 */
@Getter
@Setter
public class ObjectEvidenceInfo extends EvidenceInfo {

    /**
     * デフォルトシリアルバージョンUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * エビデンスオブジェクト.
     */
    private Object object;
}
