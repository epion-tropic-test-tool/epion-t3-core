/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.epion_t3.core.exception.SystemException;
import com.epion_t3.core.message.impl.CoreMessages;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Path;

@Slf4j
public final class YamlUtils {

    private static final YamlUtils instance = new YamlUtils();

    private static final YAMLFactory yamlFactory = new YAMLFactory();

    private static final ObjectMapper objectMapper = new ObjectMapper(yamlFactory);

    static {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
    }

    private YamlUtils() {

    }

    public static YamlUtils getInstance() {
        return instance;
    }

    public String marshal(@NonNull Object obj) {
        try {
            String yaml = objectMapper.writeValueAsString(obj);
            log.trace(yaml);
            return yaml;
        } catch (JsonProcessingException e) {
            throw new SystemException(CoreMessages.CORE_ERR_1004);
        }
    }

    public <T> T unmarshal(@NonNull String yaml) {
        try {
            var ref = new TypeReference<T>() {
            };
            return objectMapper.readValue(yaml, ref);
        } catch (IOException e) {
            throw new SystemException(CoreMessages.CORE_ERR_1004);
        }
    }

    public <T> T unmarshal(@NonNull Path yamlPath) {
        try {
            var ref = new TypeReference<T>() {
            };
            return objectMapper.readValue(yamlPath.toFile(), ref);
        } catch (IOException e) {
            throw new SystemException(CoreMessages.CORE_ERR_1004);
        }
    }

}
