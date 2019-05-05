package com.epion_t3.core.exception;

import lombok.Getter;

/**
 * Configurationが見つからない例外.
 *
 * @author takashno
 */
@Getter
public class ConfigurationNotFoundException extends SystemException {

    private String configurationId;

    public ConfigurationNotFoundException(String configurationId) {
        this.configurationId = configurationId;
    }
}
