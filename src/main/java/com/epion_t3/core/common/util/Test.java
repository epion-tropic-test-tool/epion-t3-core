package com.epion_t3.core.common.util;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Test {

    public static void main(String[] args) {

        List<Integer> intList = new ArrayList<>();
        List<String> stringList = new ArrayList<>();

        check(intList);

    }

    private static void check(List checkTarget) {

        Class<?> clazz = checkTarget.getClass().getComponentType();
        System.out.println(clazz);

        Arrays.stream(checkTarget.getClass().getTypeParameters()).forEach(x->{
            for (Type t: x.getBounds()) {
                System.out.println(t.getTypeName());
            }

        });

        //TypeVariable<Class<List>>[] av = checkTarget.getClass().getTypeParameters();
        Object obj = checkTarget.getClass().getTypeParameters();
        System.out.println(obj);

    }
}
