/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * カスタム設定定義アノテーション.
 *
 * @author takashno
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomConfigurationDefinition {

    /**
     * カスタム設定ID.
     */
    String id();

}
