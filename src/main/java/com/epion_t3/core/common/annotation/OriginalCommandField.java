/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Command定義のオリジナルフィールドの判断をするために付与するアノテーション. それ以外のなんの意味もない.<br>
 * 0.1.1にてクラス名にリネーム対応を入れた。旧クラス名は「OriginalProcessField」
 *
 * @author takashno
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface OriginalCommandField {
}
