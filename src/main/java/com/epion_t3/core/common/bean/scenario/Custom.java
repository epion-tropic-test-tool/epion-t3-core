package com.epion_t3.core.common.bean.scenario;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * カスタム定義.
 */
@Getter
@Setter
public class Custom implements Serializable {

    /**
     * DefaultSerialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * カスタムパッケージ定義.
     */
    private final Map<String, String> packages = new ConcurrentHashMap<>();

}
