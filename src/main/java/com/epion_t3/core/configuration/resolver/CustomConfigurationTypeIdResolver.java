/* Copyright (c) 2017-2021 Nozomu Takashima. */
package com.epion_t3.core.configuration.resolver;

import com.epion_t3.core.common.bean.CustomConfigurationInfo;
import com.epion_t3.core.custom.holder.CustomPackageHolder;
import com.epion_t3.core.exception.ConfigurationNotFoundException;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.IOException;

/**
 * 動的にカスタム設定情報クラスのモデルを解決するためのクラス.
 *
 * @author takahsno
 */
public class CustomConfigurationTypeIdResolver implements TypeIdResolver {

    @Override
    public void init(JavaType baseType) {
    }

    @Override
    public String idFromValue(Object value) {
        return value.getClass().getName();
    }

    @Override
    public String idFromValueAndType(Object value, Class<?> suggestedType) {
        return null;
    }

    @Override
    public String idFromBaseType() {
        return null;
    }

    /**
     * YAMLに定義された「configurationId」を利用から該当するモデルを解決し、パースに利用するモデルクラスを返却する.
     *
     * @param context
     * @param id
     * @return
     * @throws IOException
     */
    @Override
    public JavaType typeFromId(DatabindContext context, String id) throws IOException {
        TypeFactory typeFactory = (context != null) ? context.getTypeFactory() : TypeFactory.defaultInstance();
        CustomConfigurationInfo customConfigurationInfo = CustomPackageHolder.getInstance()
                .getCustomConfigurationInfo(id);
        if (customConfigurationInfo != null) {
            return typeFactory.constructType(customConfigurationInfo.getModel());
        }
        throw new ConfigurationNotFoundException(id);
    }

    @Override
    public String getDescForKnownTypeIds() {
        return null;
    }

    @Override
    public JsonTypeInfo.Id getMechanism() {
        return null;
    }
}
