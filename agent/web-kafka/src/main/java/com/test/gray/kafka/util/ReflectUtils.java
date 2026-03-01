package com.test.gray.kafka.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReflectUtils {

    private static final Logger logger = LoggerFactory.getLogger(ReflectUtils.class);


    private static Map<String/*className*/, List<Field>> classFilesMap = new HashMap<>();

    public static void setFieldValue(Object target, String fieldName, Object value) {
        Class<?> targetClass = target.getClass();
        try {
            Field field = getField(targetClass, fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            logger.error("反射设置字段失败", e);
            throw new RuntimeException(e);
        }
    }

    public static Field getField(Object target, String fieldName) {
        Class<?> targetClass = target.getClass();
        try {
            Field field = getField(targetClass, fieldName);
            field.setAccessible(true);
            return field;
        } catch (Exception e) {
            logger.error("反射获取字段Field失败", e);
            throw new RuntimeException(e);
        }
    }

    public static Object getFieldValue(Object target, String fieldName) {
        Class<?> targetClass = target.getClass();
        try {
            Field field = getField(targetClass, fieldName);
            field.setAccessible(true);
            return field.get(target);
        } catch (Exception e) {
            logger.error("反射获取字段失败", e);
            throw new RuntimeException(e);
        }
    }

    public static Object invokeMethod(Method method, Object target, Object... params) {
        try {
            return method.invoke(target, params);
        } catch (Exception e) {
            logger.error("反射调用方法失败", e);
            throw new RuntimeException(e);
        }
    }

    public static Method getMethod(Object target, String methodName, Class<?>... paramsTypes) {
        Class<?> targetClass = target.getClass();
        try {
            return targetClass.getMethod(methodName, paramsTypes);
        } catch (Exception e) {
            logger.error("反射获取方法失败", e);
            throw new RuntimeException(e);
        }
    }

    public static boolean isAssignableFrom(Object target, String className) {
        Class<?> clazz = loadClass(target, className);
        return target.getClass().isAssignableFrom(clazz);
    }


    public static Class<?> loadClass(Object target, String className) {
        ClassLoader classLoader = target.getClass().getClassLoader();
        try {
            return classLoader.loadClass(className);
        } catch (Exception e) {
            logger.error("加载类失败", e);
            throw new RuntimeException(e);
        }
    }

    public static Field getField(Class clazz, String fieldName) {
        List<Field> declaredFields = getDeclaredFields(clazz);
        for (Field field : declaredFields) {
            if (fieldName.equals(field.getName())) {
                return field;
            }
        }
        throw new RuntimeException(String.format("没找到%s field %", clazz.getName(), fieldName));
    }

    public static List<Field> getDeclaredFields(Class clazz) {
        String className = clazz.getName();
        List<Field> fieldList = classFilesMap.get(className);
        if (fieldList != null) {
            return fieldList;
        }
        fieldList = new ArrayList<>();
        getField(clazz, fieldList);
        classFilesMap.put(className, fieldList);
        return fieldList;
    }


    private static void getField(Class clazz, List<Field> fieldList) {
        Field[] fields = clazz.getDeclaredFields();
        if (fields != null) {
            for (Field field : fields) {
                fieldList.add(field);
            }
        }
        Class superClass = clazz.getSuperclass();
        if (superClass == null || "java.lang.Object".equals(superClass.getName())) {
            return;
        } else {
            getField(superClass, fieldList);
        }
    }


}
