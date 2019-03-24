package com.zomu.t.epion.tropic.test.tool.core.scenario.parser;

/**
 * @author takashno
 */
public interface ScenarioParser<Context> {

    /**
     *
     * @param context コンテキスト
     */
    void parse(final Context context);

}
