package com.example.test;

import cn.hutool.json.JSONUtil;
import com.example.utils.BeanUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

public class BeanComparedTest {

    @Test
    public void test() throws IllegalAccessException {
        List<Material> materials1 = Arrays.asList(
                Material.builder().name("1").code("1").type("1").size("1").color("1").weight("1").build(),
                Material.builder().name("2").code("2").type("2").size("2").color("2").weight("2").build(),
                Material.builder().name("3").code("3").type("3").size("3").color("3").weight("3").build()
        );
        List<Material> materials2 = Arrays.asList(
                Material.builder().name("1").code("1").type("1").size("1").color("1").weight("1").build(),
                Material.builder().name("2").code("2").type("2").size("2").color("2").weight("2").build(),
                Material.builder().name("3").code("3").type("3").size("3").color("3").weight("3").build()
        );

        OrdOrder ordOrder = new OrdOrder();
        ordOrder.setId(1L);
        ordOrder.setSn("111");
        ordOrder.setStationno("1");
        ordOrder.setMtno("1");
        ordOrder.setDoname("1");
        ordOrder.setMaterials(materials1);

        OrdOrder ordOrder2 = new OrdOrder();
        ordOrder2.setId(2L);
        ordOrder2.setSn("222");
        ordOrder2.setStationno("2");
        ordOrder2.setMtno("2");
        ordOrder2.setDoname("2");
        ordOrder2.setMaterials(materials2);


        OrdOrder ordOrder3 = new OrdOrder();
        ordOrder3.setId(3L);
        ordOrder3.setSn("111");
        ordOrder3.setStationno("2");
        ordOrder3.setMtno("2");
        ordOrder3.setDoname("2");
        ordOrder3.setMaterials(materials2);

        OrdOrder ordOrder4 = new OrdOrder();
        ordOrder4.setId(4L);
        ordOrder4.setSn("222");
        ordOrder4.setStationno("2");
        ordOrder4.setMtno("2");
        ordOrder4.setDoname("2");
        ordOrder4.setMaterials(materials2);

        // 对比这两个对象，有哪些属性不同，打印出来
        printDifferencesUsingReflection(materials1, materials2);

        List<OrdOrder> list1 = Arrays.asList(ordOrder, ordOrder2);
        List<OrdOrder> list2 = Arrays.asList(ordOrder3, ordOrder4);
        System.out.println(compareListProperties(list1, list2));

        System.out.println(ordOrder.equals(ordOrder3));

        OrdOrder ordOrder1 = new OrdOrder();
        BeanUtils.copyProperties(ordOrder, ordOrder1);
        System.out.println(ordOrder1);

        System.out.println(JSONUtil.toJsonStr(list1));

        System.out.println(BeanUtil.toJsonIgnoreCollections(list1));

    }
    public void printDifferencesUsingReflection(Object obj1, Object obj2) throws IllegalAccessException {
        Class<?> clazz = obj1.getClass();
        while (clazz != null) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Object value1 = field.get(obj1);
                Object value2 = field.get(obj2);
                if (!Objects.equals(value1, value2)) {
                    System.out.println(field.getName() + " differs: " + value1 + " vs " + value2);
                }
            }
            clazz = clazz.getSuperclass(); // 获取父类
        }
    }

    /**
     * 比较两个 List 中的对象属性是否一致，顺序可能不同
     *
     * @param list1 第一个 List
     * @param list2 第二个 List
     * @return 如果两个 List 中的对象属性一致，返回 true；否则返回 false
     */
    public static boolean compareListProperties(List<?> list1, List<?> list2) {
        if (list1 == null || list2 == null) {
            return list1 == list2;
        }

        if (list1.size() != list2.size()) {
            return false;
        }

        // 将 list1 转换为 Set
        Set<Object> set1 = new HashSet<>(list1);
        // 将 list2 转换为 Set
        Set<Object> set2 = new HashSet<>(list2);

        return set1.equals(set2);
    }


    @Test
    public void test1() throws Exception {
        OrdOrder ordOrder = new OrdOrder();
//        ordOrder.setId(1L);
        ordOrder.setSn("111");
        ordOrder.setTotal(new BigDecimal("1.1"));
        ordOrder.setCreateTime(new Date());

        System.out.println(BeanUtil.getPropertyValue(ordOrder, "id"));
        System.out.println(BeanUtil.getPropertyValue(ordOrder, "sn"));
        System.out.println(BeanUtil.getPropertyValue(ordOrder, "total"));
        System.out.println(BeanUtil.getPropertyValue(ordOrder, "createTime"));

        OrdOrder ordOrder1 = new OrdOrder();
        BeanUtil.setPropertyValue(ordOrder1, "id", BeanUtil.getPropertyValue(ordOrder, "id"));
        BeanUtil.setPropertyValue(ordOrder1, "sn", BeanUtil.getPropertyValue(ordOrder, "sn"));
        BeanUtil.setPropertyValue(ordOrder1, "total", BeanUtil.getPropertyValue(ordOrder, "total"));
        BeanUtil.setPropertyValue(ordOrder1, "createTime", BeanUtil.getPropertyValue(ordOrder, "createTime"));

        System.out.println(ordOrder1);
    }



}
