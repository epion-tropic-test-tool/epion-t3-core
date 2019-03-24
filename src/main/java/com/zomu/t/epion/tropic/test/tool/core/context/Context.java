package com.zomu.t.epion.tropic.test.tool.core.context;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.zomu.t.epion.tropic.test.tool.core.context.*;
import com.zomu.t.epion.tropic.test.tool.core.context.execute.ExecuteContext;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基底コンテキスト.
 */
public class Context implements Serializable {

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

    @Getter
    private final Map<String, CommandInfo> customCommands = new ConcurrentHashMap<>();

    @Getter
    private final Map<String, FlowInfo> customFlows = new ConcurrentHashMap<>();

    @Getter
    private final Map<String, CustomConfigurationInfo> customConfigurations = new ConcurrentHashMap<>();

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
