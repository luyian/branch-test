package com.example.test;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StreamTest {

    @Test
    public void test01() {
        List<String> strings = Arrays.asList("1", "2", "3", "4", "5");
        Optional<String> any = strings.stream().filter(s -> s.equals("6")).filter(s-> s.equals("7")).findAny();
        System.out.println(any.isPresent());

        Object xxxx = Optional.ofNullable(null).orElse("xxxx");
        System.out.println(Optional.ofNullable(null).isPresent());
        System.out.println(null + "" + null);
    }

    @Test
    public void test02() {
        BigDecimal bigDecimal1 = new BigDecimal(2.87679879);
        BigDecimal bigDecimal2 = new BigDecimal("2.87679879");
        System.out.println(bigDecimal1);
        System.out.println(bigDecimal2);
        Integer a = 1;
        System.out.println(a.equals("1"));

        System.out.println(System.currentTimeMillis());
    }

    @Test
    public void test03() {
        String template = "尊敬的${guestname}:我司与您合作的户用分布式光伏业务，在地址位于${pojectaddress}安装了\"顶好惠民\"品牌户用光伏电站，实际装机容量为${totaltotalpower}KW,其中光伏组件共有${totalquantity}块。若您对组件数量有异议的，可在收到相关信息之日起2日内拨打4009999166向我司告知。否则，视为认可短信告知的电站装机量及组件数量。";
        // 获取模板参数map
        Pattern pattern = Pattern.compile("[$][{]([^$]*?)[}]");
        Matcher matcher = pattern.matcher(template);
        Map<String, String> paramMap = new HashMap<>();
        while (matcher.find()) {
            paramMap.put(matcher.group(1), "");
        }
        paramMap.forEach((k, v) -> System.out.println(k + ":" + v));
    }

    @Test
    public void test04() {
        Integer a = 2;
        System.out.println(2 == a);
    }
}
