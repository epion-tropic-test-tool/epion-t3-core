/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.common.util;

import com.epion_t3.core.exception.SystemException;
import com.epion_t3.core.message.impl.CoreMessages;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;

@Slf4j
public final class JsonUtils {

    private static final JsonUtils instance = new JsonUtils();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
    }

    private JsonUtils() {
    }

    public static JsonUtils getInstance() {
        return instance;
    }

    public String marshal(Object obj) {
        try {
            String json = objectMapper.writeValueAsString(obj);
            log.trace(json);
            return json;
        } catch (JsonProcessingException e) {
            throw new SystemException(CoreMessages.CORE_ERR_1005);
        }
    }

    public <T> T unmarshal(String json) {
        try {
            var ref = new TypeReference<T>() {
            };
            T object = objectMapper.readValue(json, ref);
            log.trace(object.toString());
            return object;
        } catch (IOException e) {
            throw new SystemException(CoreMessages.CORE_ERR_1005);
        }
    }

    public <T> T unmarshal(Path jsonFile) {
        try {
            var ref = new TypeReference<T>() {
            };
            T object = objectMapper.readValue(jsonFile.toFile(), ref);
            log.trace(object.toString());
            return object;
        } catch (IOException e) {
            throw new SystemException(CoreMessages.CORE_ERR_1005);
        }
    }

    public String rePretty(String json) {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        try {
            var map = objectMapper.readValue(json, LinkedHashMap.class);
            var jsonString = objectMapper.writeValueAsString(map);
            log.trace(jsonString);
            return jsonString;
        } catch (IOException e) {
            throw new SystemException(CoreMessages.CORE_ERR_1005);
        }
    }

}
