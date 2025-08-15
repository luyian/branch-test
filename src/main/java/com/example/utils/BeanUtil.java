package com.example.utils;


import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.PropertyFilter;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

public class BeanUtil {

    public  static <T> T copyPropertiesByFastJson(Object source,Class<T> target) {
        if(source == null) {
            return null;
        } else {
            String jsonString = JSONObject.toJSONString(source);
            return JSONObject.parseObject(jsonString,target);
        }
    }

    public static void setPropertyValue(Object obj, String fieldName, String value) throws Exception {
        if (value == null || value.isEmpty()) {
            return;
        }

        if (obj == null || fieldName == null) {
            throw new IllegalArgumentException("Object, field name must not be null");
        }

        Field field = findField(obj.getClass(), fieldName);
        if (field == null) {
            throw new NoSuchFieldException("Field " + fieldName + " not found on object or any of its superclasses");
        }

        field.setAccessible(true);

        Class<?> fieldType = field.getType();

        if (fieldType == String.class) {
            field.set(obj, value);
        } else if (fieldType == Date.class) {
            field.set(obj, cn.hutool.core.date.DateUtil.parse(value, "yyyy-MM-dd HH:mm:ss"));
        } else if (fieldType == Integer.class || fieldType == int.class) {
            field.set(obj, Integer.parseInt(value));
        } else if (fieldType == Long.class || fieldType == long.class) {
            field.set(obj, Long.parseLong(value));
        } else if (fieldType == BigDecimal.class) {
            field.set(obj, new BigDecimal(value));
        }
    }

    /**
     * 动态获取对象属性值
     *
     * @param obj      要获取属性值的对象
     * @param fieldName 属性名称
     * @return 属性值，如果属性类型不支持或找不到字段，则返回 null
     * @throws NoSuchFieldException   如果找不到指定字段
     * @throws IllegalAccessException 如果无法访问字段
     */
    public static String getPropertyValue(Object obj, String fieldName) throws Exception {
        if (obj == null || fieldName == null) {
            throw new IllegalArgumentException("Object and field name must not be null");
        }

        Field field = findField(obj.getClass(), fieldName);
        if (field == null) {
            throw new NoSuchFieldException("Field " + fieldName + " not found on object or any of its superclasses");
        }

        field.setAccessible(true);
        if (field.get(obj) == null) {
            return null;
        }
        if (field.getType() == Date.class) {
            return DateUtil.format((Date) field.get(obj), "yyyy-MM-dd HH:mm:ss");
        } else {
            return field.get(obj).toString();
        }
    }

    private static Field findField(Class<?> clazz, String fieldName) {
        if (clazz == null) {
            return null;
        }

        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            // Continue searching in the superclass
            return findField(clazz.getSuperclass(), fieldName);
        }
    }

    /**
     * 拷贝对象属性，忽略所有集合类型字段（List、Set、Map 等）
     */
    public static String toJsonIgnoreCollections(Object source) {
        if (source == null) {
            return null;
        }

        // 自定义属性过滤器：忽略集合类型字段
        PropertyFilter filter = (obj, name, value) -> {
            try {
                Field field = obj.getClass().getDeclaredField(name);
                field.setAccessible(true);
                Object fieldValue = field.get(obj);

                // 如果字段是集合类型（List、Set、Map 等），则忽略
                return !(fieldValue instanceof Collection || fieldValue instanceof Map);
            } catch (Exception e) {
                return false;
            }
        };

        return JSON.toJSONString(source, filter);
    }

}
