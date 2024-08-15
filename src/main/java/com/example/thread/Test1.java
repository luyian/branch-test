package com.example.thread;

import com.example.demo.BaseUser;
import com.example.demo.User;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class Test1 {

    public static int duckNum = 100;

    public static void main(String[] args) {
        // 生产者
        Thread product = new Thread(() -> {
            while (duckNum < 120) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("生产者生产第" + (++duckNum) + "只烤鸭");
            }
        });

        // 消费者
        Thread consumer = new Thread(() -> {
            while (duckNum > 0) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("消费者消费第" + (--duckNum) + "只烤鸭");
                System.out.println("消费者消费第" + (--duckNum) + "只烤鸭");
            }
        });

        consumer.start();
        product.start();

    }

    @Test
    public void test01() {
        String str = " ";

        System.out.println(str.substring(str.length() - 1));
    }

    @Test
    public void test03() {
        System.out.println("test branch 3-1");
    }

    @Test
    public void test04() {
        String productno = "1212";
        String initSql = "" +
                // 初始化prdt_trafficlight，设置触发进件的电站节点
                "insert into prdt_trafficlight " +
                "select null,productid,40,0,1,null,now(),1,null,null from prdt_product where productno = '"+productno+"'; " +
                "insert into prdt_trafficlight " +
                "select null,productid,100,0,1,null,now(),1,null,null from prdt_product where productno = '"+productno+"'; " +
                "insert into prdt_trafficlight " +
                "select null,productid,500,0,1,null,now(),1,null,null from prdt_product where productno = '"+productno+"'; " +
                "insert into prdt_trafficlight " +
                "select null,productid,600,0,1,null,now(),1,null,null from prdt_product where productno = '"+productno+"';" +
                // 初始化prdt_trafficlightlinkserv
                "insert into prdt_trafficlightlinkserv " +
                "select null,t.trafficlightid,t1.tlserviceid,t1.switchtype,now(),1 " +
                "from prdt_trafficlight t " +
                "inner join prdt_product pr on t.productid = pr.productid " +
                "inner join base_orginfoextend oe on pr.operateleaseorgid = oe.orgid " +
                "inner join prdt_trafficlightservice t1 on t.stationnode = t1.stationnode " +
                "where pr.productno = '"+productno+"';";
        System.out.println(initSql);
    }

    @Test
    public void test05() {
        Integer.parseInt("1");
    }

    @Test
    public void test06() throws JSONException {
//        org.json.JSONObject res = new JSONObject();
////        res.put("result", new JSONObject().put("aaa", "bbb"));
//
//        org.json.JSONObject result = res.optJSONObject("result");
//        System.out.println(result);

        BaseUser baseUser = new BaseUser();
        baseUser.setLinkman("sun");
        System.out.println(baseUser.equals(new BaseUser()));

    }


    @Test
    public void test07() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add("zhangsan" +i);
        }

        String s = list.stream().filter(x -> x.equals("zhangsan19")).distinct().findFirst().orElse("xxxx");
        System.out.println(s);

    }

    @Test
    public void test08() {
        long a = 0L;
        Integer b = 0;
        System.out.println(b.equals(a));
    }


    @Test
    public void test09() {
        int v1c = 222;
        int v2c = 502;
        int pz = 200;

        int toc = v1c + v2c;
        int tp = toc/pz + 1;
        for (int i = 1; i <= tp; i++) {
            int ps1 = 0;
            int tmp = v1c - i*pz;
            int psz = 0;
            int off = 0;

            if (tmp >= 0) {
                psz = 0;
            } else if (tmp < 0 && tmp >= -pz) {
                psz = -tmp;
            } else if (tmp < -pz) {
                off = -tmp - pz;
                psz = pz;
            }
            System.out.println("limit " + psz + " offset " + off);

        }


    }

}
