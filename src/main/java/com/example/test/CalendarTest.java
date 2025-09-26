package com.example.test;

import cn.hutool.json.JSONUtil;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CalendarTest {

    @Test
    public void test() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        System.out.println(calendar.getTimeInMillis());
        String format = dateFormat.format(calendar.getTime());
        System.out.println(format);

        int nowMonth = calendar.get(Calendar.MONTH);
        System.out.println(nowMonth);
        calendar.add(Calendar.DAY_OF_MONTH, -3);
        System.out.println(calendar.get(Calendar.MONTH));

        System.out.println(dateFormat.format(calendar.getTime()));

    }

    @Test
    public void test2() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startMonth = simpleDateFormat.format(calendar.getTime());
        Long startTimeMillis = calendar.getTimeInMillis();
        System.out.println("month:" + startMonth + "     time: " + startTimeMillis);

        calendar.add(Calendar.DAY_OF_MONTH, -3);
        String endMonth = simpleDateFormat.format(calendar.getTime());
        Long endTimeMillis = calendar.getTimeInMillis();
        System.out.println("month:" + endMonth + "     time: " + endTimeMillis);



    }

    @Test
    public void test3() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        calendar.setTimeInMillis(1704056401233L);
        System.out.println(simpleDateFormat.format(calendar.getTime()));
    }

    public static void main(String[] args) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date dateStart = sdf.parse("14/01/2012 09:29:58");
        Date dateStop = sdf.parse("15/01/2012 10:31:48");

        long difference = dateStop.getTime() - dateStart.getTime();

        long diffDays = difference / (24 * 60 * 60 * 1000); // days
        long diffHours = (difference % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000); // remaining hours

        System.out.printf("Difference: %d days, %d hours%n", diffDays, diffHours);
    }

    @Test
    public void test04() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String endTime = simpleDateFormat.format(calendar.getTime());
        calendar.add(Calendar.DAY_OF_MONTH, -2);
        String startTime = simpleDateFormat.format(calendar.getTime());

        System.out.println(endTime + "---" + startTime);
    }

    @Test
    public void test05() {
        Date date = new Date();
        String string = "{\"createTime\":\"Fri Aug 29 15:49:57 CST 2025\", \"stationno\":\"124245\"}";
//        String string = "{\"createTime\":\"2025-01-01 23:59:59\", \"stationno\":\"124245\"}";

        OrdOrder ordOrder = JSONUtil.toBean(string, OrdOrder.class);
        System.out.println(ordOrder.getCreateTime());
        System.out.println(ordOrder.getStationno());
    }

    @Test
    public void test06() {
        int i = 0;
        System.out.println("i=" + (++i));
    }


}
