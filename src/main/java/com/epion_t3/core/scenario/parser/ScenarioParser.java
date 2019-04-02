package com.epion_t3.core.scenario.parser;

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
