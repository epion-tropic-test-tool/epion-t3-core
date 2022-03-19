/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.exception;

import lombok.Getter;

/**
 * Configurationが見つからない例外.
 *
 * @author takashno
 */
@Getter
public class ConfigurationNotFoundException extends SystemException {

    /** 設定ID. */
    private String configurationId;

    /**
     * コンストラクタ.
     * 
     * @param configurationId 設定ID
     */
    public ConfigurationNotFoundException(String configurationId) {
        this.configurationId = configurationId;
    }
}
