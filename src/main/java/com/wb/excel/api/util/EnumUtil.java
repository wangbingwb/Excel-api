package com.wb.excel.api.util;


import com.wb.excel.api.annotation.EnumValue;

import java.lang.reflect.Field;

/**
 * Created on 2014/10/10.
 *
 * @author
 * @version 0.1.0
 */
public class EnumUtil {
    /**
     * 用于判断输入的值是否属于枚举内容。
     *
     * @param clazz
     * @param value 要判断的值
     * @return true || false / 符合||不符合
     */
    public static Boolean check(Class clazz, String value) {
        if (clazz.isEnum() && value.length() > 0) {
            value = value.toLowerCase();
            Field[] fields = clazz.getFields();
            for (Field field : fields) {
                if (!field.isAnnotationPresent(EnumValue.class)) {
                    continue;
                }
                EnumValue enumValue = field.getAnnotation(EnumValue.class);
                String[] enumValues = enumValue.value();
                for (String temp : enumValues) {
                    if (temp.equals(value)) {
                        return true;
                    }
                }
            }
            return false;
        }
        return true;
    }

    /**
     * 通过指定的枚举类和value值，返回key值（未找到key时返回空字符串）
     *
     * @param clazz
     * @param value
     * @return
     */
    public static String getKey(Class clazz, String value) {
        if (clazz.isEnum()) {
            value = value.toLowerCase();
            Field[] fields = clazz.getFields();
            for (Field field : fields) {
                if (!field.isAnnotationPresent(EnumValue.class)) {
                    continue;
                }
                EnumValue enumValue = field.getAnnotation(EnumValue.class);
                String[] enumValues = enumValue.value();
                for (String temp : enumValues) {
                    if (temp.equals(value)) {
                        return field.getName();
                    }
                }
            }
        }
        return "";
    }

    /**
     * 通过指定的枚举类和key值，返回第一个value值（未指定value时会返回key的值）
     *
     * @param clazz 要获取值的枚举类
     * @param key   枚举的key
     * @return 对应的value
     */
    public static String getValue(Class clazz, String key) {
        if (clazz.isEnum()) {
            Field[] fields = clazz.getFields();
            for (Field field : fields) {
                if (!field.isAnnotationPresent(EnumValue.class)) {
                    continue;
                }
                EnumValue enumValue = field.getAnnotation(EnumValue.class);
                String[] enumValues = enumValue.value();

				/* 如果找到了key */
                if (field.getName().equals(key)) {
                    /* 如果没有注明枚举的内容,直接返回key */
                    if (enumValues.length == 1 && enumValues[0].equals("")) {
                        enumValues[0] = key;
                    }
					/* 返回 */
                    return enumValues[0];
                }
            }
        }
        return "";
    }

    /**
     * 根据索引获取
     *
     * @param <T>
     * @param clazz
     * @param ordinal
     * @return
     */
    public static <T extends Enum<T>> T valueOf(Class<T> clazz, int ordinal) {
        return clazz.getEnumConstants()[ordinal];
    }

    /**
     * 根据name获取
     *
     * @param <T>
     * @param enumType
     * @param name
     * @return
     */
    public static <T extends Enum<T>> T valueOf(Class<T> enumType, String name) {
        if (name != null && !"".equals(name)) {
            return Enum.valueOf(enumType, name);
        }
        return null;
    }
}
