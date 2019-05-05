package com.epion_t3.core.flow.resolver;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.epion_t3.core.common.bean.FlowInfo;
import com.epion_t3.core.custom.holder.CustomPackageHolder;
import com.epion_t3.core.exception.FlowNotFoundException;

import java.io.IOException;

public class FlowTypeIdResolver implements TypeIdResolver {
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

    @Override
    public JavaType typeFromId(DatabindContext context, String id) throws IOException {
        TypeFactory typeFactory = (context != null) ? context.getTypeFactory() : TypeFactory.defaultInstance();
        FlowInfo flowInfo = CustomPackageHolder.getInstance().getCustomFlowInfo(id);
        if (flowInfo != null) {
            return typeFactory.constructType(flowInfo.getModel());
        }
        throw new FlowNotFoundException(id);
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
