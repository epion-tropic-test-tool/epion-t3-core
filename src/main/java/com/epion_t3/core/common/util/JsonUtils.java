package com.epion_t3.core.common.util;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.epion_t3.core.exception.SystemException;
import com.epion_t3.core.message.impl.CoreMessages;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.LinkedHashMap;

@Slf4j
public final class JsonUtils {

    private static final JsonUtils instance = new JsonUtils();

    private JsonUtils() {
    }

    public static JsonUtils getInstance() {
        return instance;
    }

    public String marshal(Object obj) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        try {
            String json = objectMapper.writeValueAsString(obj);
            log.trace(json);
            return json;
        } catch (JsonProcessingException e) {
            throw new SystemException(CoreMessages.CORE_ERR_1005);
        }
    }

    public String rePretty(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        try {
            LinkedHashMap map = objectMapper.readValue(json, LinkedHashMap.class);
            String jsonString = objectMapper.writeValueAsString(map);
            log.trace(jsonString);
            return jsonString;
        } catch (IOException e) {
            throw new SystemException(CoreMessages.CORE_ERR_1005);
        }
    }

}
