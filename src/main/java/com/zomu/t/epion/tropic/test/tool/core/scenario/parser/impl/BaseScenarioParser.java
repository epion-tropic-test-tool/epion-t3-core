package com.zomu.t.epion.tropic.test.tool.core.scenario.parser.impl;

import com.zomu.t.epion.tropic.test.tool.core.context.Context;
import com.zomu.t.epion.tropic.test.tool.core.exception.SystemException;
import com.zomu.t.epion.tropic.test.tool.core.custom.parser.impl.BaseCustomParser;
import com.zomu.t.epion.tropic.test.tool.core.custom.parser.impl.BaseOriginalHoldParser;
import com.zomu.t.epion.tropic.test.tool.core.message.impl.CoreMessages;
import com.zomu.t.epion.tropic.test.tool.core.scenario.parser.ScenarioParser;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * シナリオ解析処理.
 */
@Slf4j
public final class BaseScenarioParser implements ScenarioParser<Context> {

    /**
     * インスタンス.
     */
    private static final BaseScenarioParser instance = new BaseScenarioParser();

    private BaseScenarioParser() {
        // Do Nothing...
    }

    /**
     * インスタンスを取得する.
     *
     * @return
     */
    public static BaseScenarioParser getInstance() {
        return instance;
    }

    /**
     * 解析処理.
     *
     * @param context
     */
    @Override
    public void parse(Context context) {

        // ルートディレクトリの存在チェック
        if (!Files.exists(Paths.get(context.getOption().getRootPath()))) {
            throw new SystemException(CoreMessages.CORE_ERR_0009, context.getOption().getRootPath());
        }

        // カスタム定義の解析
        BaseCustomParser.getInstance().parse(context);

        // オリジナルの解析
        // 全シナリオファイルをここで一度解析する
        BaseOriginalHoldParser.getInstance().parse(context);

    }

}
