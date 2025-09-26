package com.example.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 枚举转前端 select 选项工具类
 */
public class EnumSelectUtil {

    /**
     * 将枚举列表转换为前端 select 选项结构
     *
     * @param enumClass 枚举类，必须包含 value 和 name 字段
     * @return JSONArray 包含 label 和 value 的选项数组
     */
    public static JSONArray convertToSelectOptions(Class<? extends Enum<?>> enumClass) {
        List<JSONObject> options = new ArrayList<>();

        if (enumClass == null || !enumClass.isEnum()) {
            return new JSONArray();
        }

        Enum<?>[] enums = enumClass.getEnumConstants();
        for (Enum<?> e : enums) {
            try {
                Object value = enumClass.getMethod("getValue").invoke(e);
                Object name = enumClass.getMethod("getName").invoke(e);

                JSONObject option = new JSONObject();
                option.put("value", value);
                option.put("label", name);
                options.add(option);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return JSON.parseArray(JSON.toJSONString(options));
    }

    /**
     * 获取枚举所有值的集合，用于校验
     *
     * @param enumClass 枚举类
     * @return value 列表
     */
    public static List<Integer> getEnumValues(Class<? extends Enum<?>> enumClass) {
        List<Integer> values = new ArrayList<>();

        if (enumClass == null || !enumClass.isEnum()) {
            return values;
        }

        Enum<?>[] enums = enumClass.getEnumConstants();
        for (Enum<?> e : enums) {
            try {
                Integer value = (Integer) enumClass.getMethod("getValue").invoke(e);
                values.add(value);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return values;
    }

    /**
     * 获取枚举类所有项的属性名和属性值
     * @param enumClass 枚举类
     * @return 包含每个枚举项属性的Map列表
     */
    public static <T extends Enum<T>> List<Map<String, Object>> getEnumProperty(Class<T> enumClass) {
        List<Map<String, Object>> result = new ArrayList<>();

        try {
            // 调用枚举的values方法获取所有枚举项
            T[] enumConstants = enumClass.getEnumConstants();

            // 获取所有getter方法
            Method[] methods = enumClass.getDeclaredMethods();

            // 遍历每个枚举项
            for (T enumConstant : enumConstants) {
                Map<String, Object> enumMap = new HashMap<>();
                // 添加枚举名称
                enumMap.put("name", enumConstant.name());
                enumMap.put("ordinal", enumConstant.ordinal());

                // 遍历所有方法，获取属性值
                for (Method method : methods) {
                    String methodName = method.getName();
                    // 如果是getter方法且不是继承自Object类的方法
                    if ((methodName.startsWith("get") || methodName.startsWith("is"))
                            && method.getParameterCount() == 0
                            && !methodName.equals("getClass")
                            && !methodName.equals("getName")
                            && !methodName.equals("getDeclaringClass")) {

                        String fieldName = methodName.startsWith("get") ?
                                methodName.substring(3) : methodName.substring(2);
                        if (!fieldName.isEmpty()) {
                            fieldName = Character.toLowerCase(fieldName.charAt(0)) +
                                    (fieldName.length() > 1 ? fieldName.substring(1) : "");
                            Object value = method.invoke(enumConstant);
                            enumMap.put(fieldName, value);
                        }
                    }
                }

                result.add(enumMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}

