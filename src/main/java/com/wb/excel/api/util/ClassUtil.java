package com.wb.excel.api.util;


import com.wb.excel.api.annotation.ExcelCollection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created on 2015/5/28.
 *
 * @author 金洋
 * @version 2.1.0
 */
public class ClassUtil {

    public static Field[] getFields(Class clazz, boolean parentFirst) {
        List<Field> returnList = new ArrayList<>();
        Set<String> nameSet = new HashSet<>();

        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            returnList.add(field);
            nameSet.add(field.getName());
        }
        List<Field> parentList = getParentFields(clazz.getSuperclass(), nameSet, parentFirst);
        if (parentFirst) {
            parentList.addAll(returnList);
            returnList = parentList;
        } else {
            returnList.addAll(parentList);
        }

        fields = new Field[returnList.size()];

        int index = 0;
        for (Field field : returnList) {
            fields[index++] = field;
        }
        return fields;

    }

    /**
     * 获取class的 包括父类的
     *
     * @param clazz
     * @return
     */
    public static Field[] getClassFields(Class<?> clazz) {
        List<Field> list = new ArrayList<Field>();
        Field[] fields;
        do {
            fields = clazz.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                list.add(fields[i]);
            }
            clazz = clazz.getSuperclass();
        } while (clazz != Object.class && clazz != null);
        return list.toArray(fields);
    }

    /**
     * 判断是不是集合的实现类
     *
     * @param clazz
     * @return
     */
    public static boolean isCollection(Class<?> clazz) {
        return Collection.class.isAssignableFrom(clazz);
    }

    /**
     * 获取GET方法
     *
     * @param name
     * @param pojoClass
     * @return
     * @throws Exception
     */
    public static Method getMethod(String name, Class<?> pojoClass) throws Exception {
        StringBuffer getMethodName = new StringBuffer("get");
        getMethodName.append(name.substring(0, 1).toUpperCase());
        getMethodName.append(name.substring(1));
        Method method = null;
        try {
            method = pojoClass.getMethod(getMethodName.toString(), new Class[]{});
        } catch (Exception e) {
            method = pojoClass.getMethod(
                    getMethodName.toString().replace("get", "is"),
                    new Class[]{});
        }
        return method;
    }

    /**
     * 获取SET方法
     *
     * @param name
     * @param pojoClass
     * @param type
     * @return
     * @throws Exception
     */
    public static Method getMethod(String name, Class<?> pojoClass, Class<?> type) throws Exception {
        StringBuffer getMethodName = new StringBuffer("set");
        getMethodName.append(name.substring(0, 1).toUpperCase());
        getMethodName.append(name.substring(1));
        return pojoClass.getMethod(getMethodName.toString(), new Class[]{type});
    }

    /**
     * 是不是java基础类
     *
     * @param field
     * @return
     */
    public static boolean isJavaClass(Field field) {
        Class<?> fieldType = field.getType();
        boolean isBaseClass = false;
        if (fieldType.isArray()) {
            isBaseClass = false;
        } else if (fieldType.isPrimitive() || fieldType.getPackage() == null
                || fieldType.getPackage().getName().equals("java.lang")
                || fieldType.getPackage().getName().equals("java.math")
                || fieldType.getPackage().getName().equals("java.sql")
                || fieldType.getPackage().getName().equals("java.util")) {
            isBaseClass = true;
        }
        return isBaseClass;
    }

    /**
     * 通过指定的类型获取所有的成员变量
     *
     * @param clazz
     * @return
     */
    public static Field[] getFields(Class clazz) {
        return getFields(clazz, false);
    }

    private static List<Field> getParentFields(Class parentClazz, Set<String> nameSet, boolean parentFirst) {
        List<Field> fieldList = new ArrayList<>();

        if (parentClazz != null) {
            Field[] parentList = parentClazz.getDeclaredFields();
            int index = 0;
            for (Field field : parentList) {
                int beginSize = nameSet.size();
                nameSet.add(field.getName());
                int endSize = nameSet.size();

                if (endSize > beginSize) {
                    if (parentFirst) {
                        fieldList.add(index++, field);
                    } else {
                        fieldList.add(field);
                    }
                }
            }
            fieldList.addAll(getParentFields(parentClazz.getSuperclass(), nameSet, parentFirst));
        }
        return fieldList;
    }

    public static Object createObject(Class<?> clazz) {
        Object obj = null;
        Method setMethod;
        try {
            if (clazz.equals(Map.class)) {
                return new HashMap<String, Object>();
            }
            obj = clazz.newInstance();
            Field[] fields = getClassFields(clazz);
            for (Field field : fields) {
                if (isCollection(field.getType())) {
                    ExcelCollection collection = field.getAnnotation(ExcelCollection.class);
                    setMethod = getMethod(field.getName(), clazz, field.getType());
                    setMethod.invoke(obj, collection.type().newInstance());
                } else if (!isJavaClass(field)) {
                    setMethod = getMethod(field.getName(), clazz, field.getType());
                    setMethod.invoke(obj, createObject(field.getType()));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("创建对象异常");
        }
        return obj;
    }
}
