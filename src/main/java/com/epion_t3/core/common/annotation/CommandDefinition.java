package com.epion_t3.core.common.annotation;

import com.epion_t3.core.command.reporter.CommandReporter;
import com.epion_t3.core.command.reporter.impl.NoneCommandReporter;
import com.epion_t3.core.command.runner.CommandRunner;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * コマンドを表すアノテーション.
 *
 * @author takashno
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandDefinition {

    /**
     * コマンド名.
     */
    String id();

    /**
     * アサートコマンド.
     */
    boolean assertCommand() default false;

    /**
     * コマンド実行処理クラス.
     */
    Class<? extends CommandRunner> runner();

    /**
     * コマンド結果レポート出力クラス.
     *
     * @return
     */
    Class<? extends CommandReporter> reporter() default NoneCommandReporter.class;

}
