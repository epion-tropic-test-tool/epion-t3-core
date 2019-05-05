package com.epion_t3.core.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Process定義のオリジナルフィールドの判断をするために付与するアノテーション.
 * それ以外のなんの意味もない.
 *
 * @author takashno
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface OriginalProcessField {
}
