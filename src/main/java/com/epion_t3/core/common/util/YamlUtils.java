package com.epion_t3.core.common.util;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.epion_t3.core.exception.SystemException;
import com.epion_t3.core.message.impl.CoreMessages;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class YamlUtils {

    private static final YamlUtils instance = new YamlUtils();

    private YamlUtils() {

    }

    public static YamlUtils getInstance() {
        return instance;
    }

    public String marshal(Object obj) {
        YAMLFactory yamlFactory = new YAMLFactory();
        ObjectMapper objectMapper = new ObjectMapper(yamlFactory);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        try {
            String yaml = objectMapper.writeValueAsString(obj);
            log.trace(yaml);
            return yaml;
        } catch (JsonProcessingException e) {
            throw new SystemException(CoreMessages.CORE_ERR_1004);
        }
    }

}
