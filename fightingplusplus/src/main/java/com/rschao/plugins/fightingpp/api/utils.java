package com.rschao.plugins.fightingpp.api;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class utils {
    public static List<Field> getStaticFieldsOfType(Class<?> clazz, Class<?> fieldType) {
    List<Field> staticFields = new ArrayList<>();
    Field[] fields = clazz.getDeclaredFields();
    for (Field field : fields) {
        if (Modifier.isStatic(field.getModifiers()) && field.getType().equals(fieldType)) {
            staticFields.add(field);
        }
    }
    return staticFields;
    }
}
