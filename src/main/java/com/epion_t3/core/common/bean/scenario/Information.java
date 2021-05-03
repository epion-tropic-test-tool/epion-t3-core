/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.common.bean.scenario;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Information implements Serializable {

    /**
     * デフォルトシリアルバージョンUID.
     */
    private static final long serialVersionUID = 1L;

    /** シナリオID. */
    @NonNull
    private String id;

    /** テスト管理バージョン. */
    private String version;

    /** シナリオ概要. */
    private String summary;

    /** シナリオ詳細. */
    private String description;

}
