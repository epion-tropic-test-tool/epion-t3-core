package com.zomu.t.epion.tropic.test.tool.core.command.resolver;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.zomu.t.epion.tropic.test.tool.core.context.CommandInfo;
import com.zomu.t.epion.tropic.test.tool.core.exception.CommandCanNotResolveException;
import com.zomu.t.epion.tropic.test.tool.core.exception.CommandNotFoundException;
import com.zomu.t.epion.tropic.test.tool.core.holder.CustomPackageHolder;

import java.io.IOException;

/**
 * 動的にコマンドクラスのモデルを解決するためのクラス.
 *
 * @author takahsno
 */
public class CommandTypeIdResolver implements TypeIdResolver {
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
     * YAMLに定義されたIDを利用から該当するモデルを解決し、パースに利用するモデルクラスを返却する.
     *
     * @param context
     * @param id
     * @return
     * @throws IOException
     */
    @Override
    public JavaType typeFromId(DatabindContext context, String id) throws IOException {
        TypeFactory typeFactory = (context != null) ? context.getTypeFactory() : TypeFactory.defaultInstance();
        CommandInfo commandInfo = CustomPackageHolder.getInstance().getCustomCommandInfo(id);
        if (commandInfo != null) {
            return typeFactory.constructType(commandInfo.getModel());
        }
        throw new CommandCanNotResolveException(id);
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
