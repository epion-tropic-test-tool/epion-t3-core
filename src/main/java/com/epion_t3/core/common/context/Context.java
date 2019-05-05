package com.epion_t3.core.common.context;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.epion_t3.core.common.bean.Option;
import com.epion_t3.core.common.bean.Original;
import lombok.Getter;

import java.io.Serializable;

/**
 * 基底コンテキスト.
 */
public class Context implements Serializable {

    /**
     * デフォルトシリアルバージョンUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * YAML変換用の共通ObjectMapper.
     */
    @Getter
    private final ObjectMapper objectMapper;

    /**
     * YAMLを読み込んだオリジナル
     */
    @Getter
    private final Original original = new Original();

    /**
     * 実行引数オプション.
     */
    @Getter
    private final Option option;

    public Context() {
        this.option = createOption();
        this.objectMapper = createObjectMapper();
    }

    /**
     * ObjectMapperに手を加えたい場合は、オーバーライドすること.
     *
     * @return {@link ObjectMapper}
     */
    protected ObjectMapper createObjectMapper() {
        YAMLFactory yamlFactory = new YAMLFactory();
        ObjectMapper objectMapper = new ObjectMapper(yamlFactory);

        // 知らない要素は無視する
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return objectMapper;
    }

    /**
     * Optionの生成に手を加えたい場合は、オーバーライドすること.
     *
     * @return {@link Option}
     */
    protected Option createOption() {
        return new Option();
    }


}
