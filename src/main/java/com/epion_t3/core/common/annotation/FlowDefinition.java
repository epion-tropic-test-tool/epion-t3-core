package com.epion_t3.core.common.annotation;

import com.epion_t3.core.flow.runner.FlowRunner;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Flowを表すアノテーション.
 *
 * @author takashno
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface FlowDefinition {

    /**
     * FlowID.
     */
    String id();

    /**
     * Flow実行処理クラス.
     */
    Class<? extends FlowRunner> runner();

}
