package com.epion_t3.core.custom.parser;

import com.epion_t3.core.context.Context;

/**
 * 個別対象用の解析クラス.
 *
 * @author takashno
 */
public interface IndividualTargetParser<C extends Context> {

    /**
     *
     * @param context
     */
    void parse(final C context);

    /**
     *
     * @param context
     * @param fileNamePattern
     */
    void parse(final C context, final String fileNamePattern);


}
