/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.common.bean;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.Serializable;

/**
 * カスタム設定情報.
 *
 * @author takashno
 */
@Getter
@Setter
@Builder
public class CustomConfigurationInfo implements Serializable {

    /**
     * DefaultSerialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * ID.
     */
    @NonNull
    private String id;

    /**
     * 概要.
     */
    private String summary;

    /**
     * 詳細.
     */
    private String description;

    /**
     * 対応するモデルクラス.
     */
    @NonNull
    private Class model;

}
