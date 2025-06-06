package com.example.funtion;

import cn.hutool.json.JSONUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


public class PrintUtil {
    public static <T extends BaseSearch> void printPageData(QueryFactory<T> queryFactory, T search) {
        List<? extends BaseSearch> resultData = queryFactory.queryPage(search);
        String jsonStr = JSONUtil.toJsonStr(resultData);
        System.out.println(jsonStr);

        System.out.println("================= detail ====================");
        resultData.forEach(item -> getAllFieldsAndValues(item).forEach(System.out::println));
    }

    public static <T> List<String> getAllFieldsAndValues(T object) {
        List<String> result = new ArrayList<>();

        if (object == null) {
            return result;
        }

        Class<?> clazz = object.getClass();

        while (clazz != null && clazz != Object.class) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true); // 允许访问私有字段
                try {
                    Object value = field.get(object);
                    result.add("Field: " + field.getName() + ", Value: " + value);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            clazz = clazz.getSuperclass(); // 遍历父类
        }

        return result;
    }





    public interface QueryFactory<T extends BaseSearch> {
        List<? extends BaseSearch> queryPage(T search);
    }
}
