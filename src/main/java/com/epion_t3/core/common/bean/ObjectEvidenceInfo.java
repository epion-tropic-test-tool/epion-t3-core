/* Copyright (c) 2017-2019 Nozomu Takashima. */
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
